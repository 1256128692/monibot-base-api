package cn.shmedo.monitor.monibotbaseapi.util.sensor;

import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.iot.base.FieldType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.util.FieldUtil;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import org.influxdb.dto.QueryResult;

import java.sql.Timestamp;
import java.util.*;

public class InfluxSensorDataUtil {


    public static List<Map<String, Object>> parseResult(QueryResult queryResult,
                                                        List<String> selectField) {
        if (queryResult.hasError()) {
            throw new RuntimeException(queryResult.getError());
        }
        if (CollectionUtil.isNullOrEmpty(queryResult.getResults()) || CollectionUtil.isNullOrEmpty(selectField)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new LinkedList<>();
        queryResult.getResults().forEach(r -> {
            List<Map<String, Object>> itemResult = parseSingleResult(r, selectField);
            if (!CollectionUtil.isNullOrEmpty(itemResult)) {
                result.addAll(itemResult);
            }
        });
        return result;
    }

    private static List<Map<String, Object>> parseSingleResult(QueryResult.Result result,
                                                               List<String> selectField) {
        if (result.hasError()) {
            throw new RuntimeException(result.getError());
        }
        List<QueryResult.Series> seriesList = result.getSeries();
        if (CollectionUtil.isNullOrEmpty(seriesList)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> returnResult = new LinkedList<>();
        seriesList.forEach(s -> {
            List<Map<String, Object>> itemResult = parseSingleSeries(s, selectField);
            if (!CollectionUtil.isNullOrEmpty(itemResult)) {
                returnResult.addAll(itemResult);
            }
        });
        return returnResult;
    }

    private static List<Map<String, Object>> parseSingleSeries(QueryResult.Series series,
                                                               List<String> selectField) {
        Map<String, String> tagMap = series.getTags();
        List<String> columns = series.getColumns();
        List<List<Object>> rowList = series.getValues();
        if (CollectionUtil.isNullOrEmpty(rowList)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> result = new LinkedList<>();
        rowList.forEach(row -> {
            Map<String, Object> data = new HashMap<>();
            result.add(data);

            if (tagMap != null && tagMap.containsKey(DbConstant.SENSOR_ID_TAG)) {
                data.put(DbConstant.SENSOR_ID_FIELD_TOKEN, Integer.parseInt(tagMap.get(DbConstant.SENSOR_ID_TAG)));
            }
            for (int i = 0; i < row.size(); i++) {
                String columnName = columns.get(i);
                Object value = row.get(i);

                if (DbConstant.TIME_FIELD.equals(columnName)) {
                    String timeString = parseInfluxTime(value);
                    data.put(DbConstant.TIME_FIELD, timeString);
                } else if (DbConstant.SENSOR_ID_TAG.equals(columnName)) {
                    data.put(DbConstant.SENSOR_ID_FIELD_TOKEN, Integer.parseInt(value.toString()));
                } else {
                    boolean contains = selectField.contains(columnName);
                    if (contains) {
                        data.put(columnName, value);
                    }
                }
            }
        });
        return result;
    }

    /**
     * 计算列名 和 对应的字段名
     *
     * @param fieldSelectInfoList
     * @return
     */
    private static Map<String, Tuple<String, FieldType>> calculateColumnNameFieldTokenAndType(List<FieldSelectInfo> fieldSelectInfoList) {
        Map<String, Tuple<String, FieldType>> result = new HashMap<>();
        if (fieldSelectInfoList.get(0).getFieldTypeOrder() == null) {
            FieldUtil.calculateFieldTypeOrder(fieldSelectInfoList);
        }
        fieldSelectInfoList.forEach(fi -> {
            String columnName = fi.getFieldType().getColumnPrefix() + fi.getFieldTypeOrder();
            Tuple<String, FieldType> tokenType = new Tuple<>(fi.getFieldToken(), fi.getFieldType());
            result.put(columnName, tokenType);
        });
        return result;
    }

    private static String parseInfluxTime(Object time) {
        if (time instanceof Double) {
            Double timeDouble = (Double) time;
            long milli = timeDouble.longValue();
            Timestamp timestamp = new Timestamp(milli);
            return TimeUtil.getMilliDefaultFormatter().format(timestamp);
        } else {
            throw new RuntimeException("unknown time value");
        }
    }

    public static List<Map<String, Object>> parseCurrentRainResult(QueryResult queryResult) {

        if (queryResult.hasError()) {
            throw new RuntimeException(queryResult.getError());
        }
        if (CollectionUtil.isNullOrEmpty(queryResult.getResults())) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isNullOrEmpty(queryResult.getResults().get(0).getSeries())){
            return Collections.emptyList();
        }

        List<Map<String, Object>> resultInfolList = new LinkedList<>();
        for (int i = 0; i < queryResult.getResults().size(); i++) {
            // 单挑查询记录内容
            QueryResult.Result result = queryResult.getResults().get(i);
            List<QueryResult.Series> series = result.getSeries();
            for (int j = 0; j < series.size(); j++) {
                QueryResult.Series resultContent = series.get(j);

                // 分组信息
                Map<String, String> tagMap = resultContent.getTags();
                // 字段列
                List<String> columns = resultContent.getColumns();
                // 数值
                List<List<Object>> rowList = resultContent.getValues();
                if (CollectionUtil.isNullOrEmpty(rowList)) {
                    return Collections.emptyList();
                }

                rowList.forEach(row -> {
                    Map<String, Object> data = new HashMap<>();
                    resultInfolList.add(data);

                    if (tagMap != null && tagMap.containsKey(DbConstant.SENSOR_ID_TAG)) {
                        data.put(DbConstant.SENSOR_ID_FIELD_TOKEN, Integer.parseInt(tagMap.get(DbConstant.SENSOR_ID_TAG)));
                    }
                    for (int k = 0; k < row.size(); k++) {
                        String columnName = columns.get(k);
                        Object value = row.get(k);

                        if (DbConstant.SENSOR_ID_TAG.equals(columnName)) {
                            data.put(DbConstant.SENSOR_ID_FIELD_TOKEN, Integer.parseInt(value.toString()));
                        } else if ("currentRainfall".equals(columnName)) {
                            data.put(columnName, value);
                        }
                    }
                });
            }
        }
        return resultInfolList;
    }
}
