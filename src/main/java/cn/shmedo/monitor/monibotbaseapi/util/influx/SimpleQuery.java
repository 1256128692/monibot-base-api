package cn.shmedo.monitor.monibotbaseapi.util.influx;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 简单的InfluxDB查询构造器<br/>
 * <br/>
 * <b>示例：</b><br/>
 * <pre>
 * List<Dict> exec = SimpleQuery.of("tb_10_data")
 *          .in("sid", "488", "310")
 *          .orderByDesc("time")
 *          .groupBy("sid")
 *          .limit(1)
 *          .query(influxDB);
 * # 等效SQL: SELECT * FROM tb_10_data WHERE  ( ( sid = '488' ) OR ( sid = '310' ) )  GROUP BY sid ORDER BY time DESC LIMIT 1 tz('PRC')
 * </pre>
 */
@Slf4j
public class SimpleQuery {
    private final Set<String> select;
    private final List<Object> condition;
    private final List<String> group;
    private final List<Tuple2<List<String>, String>> order;
    private Tuple2<Integer, Integer> limit;

    private final StringBuilder sql;
    private final String table;

    public SimpleQuery(@Nonnull String table) {
        this.condition = new ArrayList<>();
        this.select = new HashSet<>();
        this.order = new ArrayList<>();
        this.group = new ArrayList<>();
        this.sql = new StringBuilder();
        this.table = table;
    }

    public SimpleQuery(@Nonnull String table, String... columns) {
        this.condition = new ArrayList<>();
        this.select = new HashSet<>();
        this.order = new ArrayList<>();
        this.group = new ArrayList<>();
        this.sql = new StringBuilder();
        this.table = table;
        Optional.ofNullable(columns).filter(e -> e.length > 0).ifPresent(this::select);
    }

    public static SimpleQuery of(@Nonnull String table, String... columns) {
        return new SimpleQuery(table, columns);
    }

    public static SimpleQuery of(@Nonnull String table) {
        return new SimpleQuery(table);
    }

    public String sql() {
        List<String> sqlParts = new ArrayList<>();
        sqlParts.add(SQLSymbol.SELECT);
        sqlParts.add(select.isEmpty() ? SQLSymbol.WILDCARD : String.join(SQLSymbol.COMMA, select));
        sqlParts.add(SQLSymbol.FROM);
        sqlParts.add(this.table);

        Optional.ofNullable(condition).filter(e -> !e.isEmpty())
                .ifPresent(e -> {
                    sqlParts.add(SQLSymbol.WHERE);
                    sqlParts.add(condition());
                });

        Optional.ofNullable(group).filter(e -> !e.isEmpty())
                .ifPresent(e -> {
                    sqlParts.add(SQLSymbol.GROUP_BY);
                    sqlParts.add(String.join(SQLSymbol.COMMA, e));
                });

        Optional.ofNullable(order).filter(e -> !e.isEmpty())
                .ifPresent(e -> {
                    sqlParts.add(SQLSymbol.ORDER_BY);
                    sqlParts.add(e.stream().map(item -> String.join(SQLSymbol.COMMA, item.getT1()) +
                            SQLSymbol.SPACE + item.getT2()).collect(Collectors.joining(SQLSymbol.COMMA)));
                });

        Optional.ofNullable(limit).ifPresent(e -> {
            if (e.getT2() > 0) {
                sqlParts.add(SQLSymbol.LIMIT);
                sqlParts.add(e.getT2().toString());
            }
            sqlParts.add(SQLSymbol.OFFSET);
            sqlParts.add(e.getT1().toString());
        });
        sqlParts.add(SQLSymbol.TIME_ZONE);
        return String.join(SQLSymbol.SPACE, sqlParts);
    }

    /**
     * 执行查询
     *
     * @param client {@link InfluxDB}
     * @return {@link QueryResult}
     */
    public QueryResult exec(@Nonnull InfluxDB client) {
        Query sql = new Query(sql());
        log.info("SQL => {}", sql.getCommand());
        return client.query(sql);
    }

    /**
     * 执行查询 并通过函数转换结果
     *
     * @param client {@link InfluxDB}
     * @param func   转换函数
     * @return 结果
     * @see #exec(InfluxDB)
     */
    public <R> List<R> exec(@Nonnull InfluxDB client, Function<QueryResult.Series, Stream<R>> func) {
        return exec(client).getResults().stream()
                .filter(e -> e != null && CollUtil.isNotEmpty(e.getSeries()))
                .flatMap(e -> e.getSeries().stream().filter(Objects::nonNull).flatMap(func)).toList();
    }

    /**
     * 执行查询 并将结果转换为 {@code List<Dict>}
     *
     * @param client {@link InfluxDB}
     * @return {@code List<Dict>}
     * @see #exec(InfluxDB, Function)
     */
    public List<Dict> query(@Nonnull InfluxDB client) {
        return exec(client, series -> {
            List<String> columns = series.getColumns();
            return series.getValues().stream().map(value -> {
                Dict item = Dict.create();
                IntStream.range(0, value.size()).forEach(e -> item.put(columns.get(e), value.get(e)));
                Optional.ofNullable(series.getTags()).ifPresent(item::putAll);
                return item;
            });
        });
    }

    /**
     * 执行查询 并将每行数据转换为对象
     *
     * @param client {@link InfluxDB}
     * @param clazz  对象类型
     * @return {@code List<T>}
     */
    public <T> List<T> query(@Nonnull InfluxDB client, Class<T> clazz) {
        return query(client).stream().map(e -> e.toBean(clazz)).toList();
    }

    /**
     * 返回单列数据
     *
     * @param client {@link InfluxDB}
     * @param column 列名
     * @param clazz  列对应值的类型
     */
    public <T> List<T> column(@Nonnull InfluxDB client, String column, Class<T> clazz) {
        return query(client).stream().map(e -> Convert.convert(clazz, e.get(column))).toList();
    }

    /**
     * 返回首列数据，未指定查询列时将抛出异常
     *
     * @see #column(InfluxDB, String, Class)
     */
    public <T> List<T> column(@Nonnull InfluxDB client, @Nonnull Class<T> clazz) {
        Assert.notEmpty(select, "must select column");
        return column(client, CollUtil.getFirst(select), clazz);
    }

    /**
     * 返回首行数据，不存在时返回null
     *
     * @param client {@link InfluxDB}
     * @return {@link Dict}
     * @see #row(InfluxDB, Class)
     */
    public Dict row(@Nonnull InfluxDB client) {
        return CollUtil.getFirst(query(client));
    }

    /**
     * 返回首行数据，并转换为Bean
     *
     * @param client {@link InfluxDB}
     * @param clazz  值的类型
     * @return bean
     * @see #row(InfluxDB)
     */
    public <T> T row(@Nonnull InfluxDB client, Class<T> clazz) {
        Dict row = row(client);
        return row != null ? row.toBean(clazz) : null;
    }

    /**
     * 返回首行首列数据，不存在时返回默认值
     *
     * @param client       {@link InfluxDB}
     * @param defaultValue 默认值
     * @return 结果
     * @see #cell(InfluxDB)
     */
    public <T> T cell(@Nonnull InfluxDB client, T defaultValue) {
        String firstColName = CollUtil.getFirst(select);
        Dict row = row(client);
        return row == null ? defaultValue : row.get(firstColName, defaultValue);
    }

    /**
     * 返回首行首列数据，不存在时返回null
     *
     * @param client {@link InfluxDB}
     * @return 结果
     * @see #cell(InfluxDB, Object)
     */
    public Object cell(@Nonnull InfluxDB client) {
        String firstColName = CollUtil.getFirst(select);
        Dict row = row(client);
        return row == null ? null : row.get(firstColName);
    }

    /**
     * 返回指定字段数量，不存在时返回0<br/>
     * 当字段不存在时
     *
     * @param client {@link InfluxDB}
     * @param column 列名，必须为存在，不能是 *
     * @return 数量
     */
    public Long count(@Nonnull InfluxDB client, @Nonnull String column) {
        this.select.clear();
        this.select.add(SQLSymbol.COUNT + SQLSymbol.LEFT_BRACKET + column + SQLSymbol.RIGHT_BRACKET);

        Dict row = row(client);
        return row == null || !row.containsKey(SQLSymbol.COUNT) ? 0L : row.getLong(SQLSymbol.COUNT);
    }

    private Object serializeValue(Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return value;
        }
        if (value instanceof Date date) {
            value = DateUtil.format(date, DatePattern.NORM_DATETIME_PATTERN);
        } else if (value instanceof LocalDateTime dateTime) {
            value = dateTime.format(DatePattern.NORM_DATETIME_FORMATTER);
        } else if (value instanceof LocalDate localDate) {
            value = localDate.format(DatePattern.NORM_DATE_FORMATTER);
        } else if (value instanceof LocalTime localTime) {
            value = localTime.format(DatePattern.NORM_TIME_FORMATTER);
        }
        return "'" + value + "'";
    }

    private String condition() {
        return StrUtil.format(sql.toString(), condition.stream().map(this::serializeValue).toArray());
    }

    private SimpleQuery condition(@Nonnull String column, Object value, @Nonnull String symbol) {
        sql.append(sql.isEmpty() || StrUtil.endWithAny(sql, SQLSymbol.AND, SQLSymbol.OR) ? SQLSymbol.EMPTY : SQLSymbol.AND)
                .append(SQLSymbol.SPACE)
                .append(column).append(SQLSymbol.SPACE)
                .append(symbol).append(SQLSymbol.SPACE);
        if (value != null) {
            sql.append(SQLSymbol.PLACEHOLDER).append(SQLSymbol.SPACE);
            condition.add(value);
        }
        return this;
    }

    private SimpleQuery relation(@Nonnull Consumer<SimpleQuery> consumer, String symbol) {
        SimpleQuery child = new SimpleQuery(this.table);
        consumer.accept(child);
        if (!child.sql.isEmpty()) {
            sql.append(sql.isEmpty() ? SQLSymbol.EMPTY : symbol).append(SQLSymbol.SPACE)
                    .append(SQLSymbol.LEFT_BRACKET).append(child.sql).append(SQLSymbol.RIGHT_BRACKET).append(SQLSymbol.SPACE);
        }
        condition.addAll(child.condition);
        return this;
    }

    /**
     * {@code ORDER BY xxx ASC|DESC}
     */
    public SimpleQuery orderBy(boolean asc, @Nonnull String... columns) {
        Optional.of(columns).filter(e -> e.length > 0).ifPresent(e -> {
            Tuple2<List<String>, String> item = Tuples.of(Arrays.stream(e).toList(),
                    asc ? SQLSymbol.ASC : SQLSymbol.DESC);
            this.order.add(item);
        });
        return this;
    }

    /**
     * {@code ORDER BY xxx,xxx ASC}
     */
    public SimpleQuery orderBy(@Nonnull String... columns) {
        return orderBy(true, columns);
    }

    /**
     * {@code ORDER BY xxx,xxx,xxx DESC}
     */
    public SimpleQuery orderByDesc(@Nonnull String... columns) {
        return orderBy(false, columns);
    }

    /**
     * {@code ORDER BY xxx,xxx ASC}
     */
    public SimpleQuery orderByAsc(@Nonnull String... columns) {
        return orderBy(true, columns);
    }

    /**
     * {@code LIMIT offset, limit}
     */
    public SimpleQuery limit(@Nonnull Integer offset, @Nonnull Integer limit) {
        assert offset >= 0 && limit > 0;
        this.limit = Tuples.of(offset, limit);
        return this;
    }

    /**
     * {@code LIMIT limit}
     */
    public SimpleQuery limit(@Nonnull Integer limit) {
        assert limit > 0;
        this.limit = Tuples.of(0, limit);
        return this;
    }

    /**
     * 每次调用都会清空之前的查询列<br/>
     * {@code SELECT A,B,C...}
     */
    public SimpleQuery select(@Nonnull String... columns) {
        Optional.of(columns).filter(e -> e.length > 0)
                .ifPresent(e -> {
                    select.clear();
                    select.addAll(Arrays.asList(columns));
                });
        return this;
    }

    /**
     * 每次调用都会清空之前的查询列<br/>
     * {@code SELECT A,B,C...}
     */
    public SimpleQuery select(@Nonnull List<String> columns) {
        Optional.of(columns).filter(e -> !e.isEmpty())
                .ifPresent(select::addAll);
        return this;
    }

    /**
     * {@code AND}
     */
    public SimpleQuery and(@Nonnull Consumer<SimpleQuery> consumer) {
        return relation(consumer, SQLSymbol.AND);
    }

    /**
     * {@code OR}
     */
    public SimpleQuery or(@Nonnull Consumer<SimpleQuery> consumer) {
        return relation(consumer, SQLSymbol.OR);
    }

    /**
     * {@code OR}
     */
    public SimpleQuery or() {
        sql.append(sql.isEmpty() ? SQLSymbol.EMPTY : SQLSymbol.OR);
        return this;
    }

    /**
     * {@code AND}
     */
    public SimpleQuery and() {
        sql.append(sql.isEmpty() ? SQLSymbol.EMPTY : SQLSymbol.AND);
        return this;
    }

    /**
     * {@code column = value}
     */
    public <S> SimpleQuery eq(@Nonnull String column, @Nonnull S value) {
        return ObjectUtil.isNotEmpty(value) ? condition(column, value, SQLSymbol.EQ) : this;
    }

    /**
     * {@code column != value}
     */
    public <S> SimpleQuery ne(@Nonnull String column, @Nonnull S value) {
        return ObjectUtil.isNotEmpty(value) ? condition(column, value, SQLSymbol.NE) : this;
    }

    /**
     * {@code column > value}
     */
    public <S> SimpleQuery gt(@Nonnull String column, @Nonnull S value) {
        return ObjectUtil.isNotEmpty(value) ? condition(column, value, SQLSymbol.GT) : this;
    }

    /**
     * {@code column >= value}
     */
    public <S> SimpleQuery gte(@Nonnull String column, @Nonnull S value) {
        return ObjectUtil.isNotEmpty(value) ? condition(column, value, SQLSymbol.GE) : this;
    }

    /**
     * {@code column < value}
     */
    public <S> SimpleQuery lt(@Nonnull String column, @Nonnull S value) {
        return ObjectUtil.isNotEmpty(value) ? condition(column, value, SQLSymbol.LT) : this;
    }

    /**
     * {@code column <= value}
     */
    public <S> SimpleQuery lte(@Nonnull String column, @Nonnull S value) {
        return ObjectUtil.isNotEmpty(value) ? condition(column, value, SQLSymbol.LE) : this;
    }

    /**
     * {@code column IN (value, value, ...)}
     */
    public <S> SimpleQuery in(@Nonnull String column, @Nonnull Collection<S> values) {

        return and(e -> {
            values.forEach(item -> {
                e.or(i -> i.eq(column, item));
            });
        });
    }

    /**
     * {@code column IN (value, value, ...)}
     */
    @SafeVarargs
    public final <S> SimpleQuery in(@Nonnull String column, @Nonnull S... values) {
        return in(column, Arrays.asList(values));
    }

    /**
     * {@code column NOT IN (value, value, ...)}
     */
    public final <S> SimpleQuery notIn(@Nonnull String column, @Nonnull Collection<S> values) {
        return and(e -> {
            values.forEach(item -> {
                e.and(i -> i.ne(column, item));
            });
        });
    }

    /**
     * {@code column NOT IN (value, value, ...)}
     */
    @SafeVarargs
    public final <S> SimpleQuery notIn(@Nonnull String column, @Nonnull S... values) {
        return notIn(column, Arrays.asList(values));
    }

    /**
     * {@code column BETWEEN value1 AND value2}
     */
    public final <S> SimpleQuery between(@Nonnull String column, @Nonnull S value1, @Nonnull S value2) {
        return and(e -> {
            e.gte(column, value1).lte(column, value2);
        });
    }

    /**
     * {@code column NOT BETWEEN value1 AND value2}
     */
    public final <S> SimpleQuery notBetween(@Nonnull String column, @Nonnull S value1, @Nonnull S value2) {
        return and(e -> {
            e.lte(column, value1).or().gte(column, value2);
        });
    }

    /**
     * {@code column LIKE CONCAT '%value%'}
     */
    public final <S> SimpleQuery like(@Nonnull String column, @Nonnull S value) {
        return condition(column, StrUtil.format("~/.*{}.*/ ", value), SQLSymbol.LIKE);
    }

    /**
     * {@code GROUP BY column1, column2, ...}
     */
    public final SimpleQuery groupBy(@Nonnull String... columns) {
        Optional.of(columns).filter(e -> e.length > 0).ifPresent(e -> group.addAll(Arrays.asList(columns)));
        return this;
    }

    public interface SQLSymbol {
        String EMPTY = "";
        String COMMA = ", ";
        String SPACE = " ";
        String SELECT = "SELECT";
        String WILDCARD = "*";
        String PLACEHOLDER = "{}";
        String AND = "AND";
        String OR = "OR";
        String LIKE = "LIKE";
        String NOT_LIKE = "NOT LIKE";
        String BETWEEN = "BETWEEN";
        String NOT_BETWEEN = "NOT BETWEEN";
        String IN = "IN";
        String NOT_IN = "NOT IN";
        String IS_NULL = "IS NULL";
        String IS_NOT_NULL = "IS NOT NULL";
        String EQ = "=";
        String NE = "<>";
        String GT = ">";
        String GE = ">=";
        String LT = "<";
        String LE = "<=";
        String ASC = "ASC";
        String DESC = "DESC";
        String ORDER_BY = "ORDER BY";
        String GROUP_BY = "GROUP BY";
        String HAVING = "HAVING";
        String PERCENT = "%";
        String LEFT_BRACKET = "(";
        String RIGHT_BRACKET = ")";
        String CONCAT = "CONCAT";
        String FROM = "FROM";
        String WHERE = "WHERE";
        String LIMIT = "LIMIT";
        String OFFSET = "OFFSET";
        String TIME_ZONE = "tz('PRC')";
        String COUNT = "count";
    }
}