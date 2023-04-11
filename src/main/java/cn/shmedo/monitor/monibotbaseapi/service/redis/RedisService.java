package cn.shmedo.monitor.monibotbaseapi.service.redis;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * springboot redis 工具类<br>
 * 使用 {@link StringRedisTemplate} 在设/取值前后进行手动序列化/反序列化<br>
 * 该工具类只提供了常用的方法，如有需要，可调用 {@link RedisService#getTemplate()} 的相关方法
 **/
//@Component
public class RedisService {

    private final StringRedisTemplate template;

//    @Autowired
    public RedisService(StringRedisTemplate template) {
        this.template = template;
    }

    public StringRedisTemplate getTemplate() {
        return template;
    }

    /**
     * 按key进行删除
     *
     * @param key key
     * @return {@code true} 删除成功
     */
    public boolean del(String key) {
        return Boolean.TRUE.equals(template.delete(key));
    }

    /**
     * 按key批量删除
     *
     * @param keys key集合
     * @return 被移除的键的数量
     */
    public long del(Collection<String> keys) {
        Long data = template.delete(keys);
        return null != data ? data : 0;
    }

    /**
     * 按key批量删除
     *
     * @param keys key数组
     * @return 被移除的键的数量
     */
    public long del(String... keys) {
        return del(Arrays.asList(keys));
    }

    /**
     * 判断key是否存在
     *
     * @param key key
     * @return {@code true} 存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    /**
     * 按前缀判断key是否存在
     *
     * @param prefix 前缀
     * @return {@code true} 存在
     */
    public boolean hasKeyPrefix(String prefix) {
        return !keys(prefix + '*').isEmpty();
    }

    /**
     * 设置过期时间
     *
     * @param key     key
     * @param timeout 过期时间
     * @param unit    时间单位 {@link TimeUnit}
     * @return 结果
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(template.expire(key, timeout, unit));
    }

    /**
     * 设置过期时间，时间单位为秒
     *
     * @param key     key
     * @param timeout 过期时间
     * @return 结果
     */
    public boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间，key不存在时会返回0
     *
     * @param key  key
     * @param unit 时间单位
     * @return 过期时间
     */
    public long ttl(String key, TimeUnit unit) {
        Long result = null;
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            result = template.getExpire(key, unit);
        }
        return result == null ? 0 : result;
    }

    /**
     * 获取过期时间，单位秒，key不存在时会返回0
     *
     * @param key key
     * @return 过期时间
     */
    public long ttl(String key) {
        return ttl(key, TimeUnit.SECONDS);
    }

    /**
     * 模糊查询 key，支持通配符 {@code *、?、[]}<br>
     * {@code *} 通配任意多个字符<br>
     * {@code ?} 通配单个字符<br>
     * {@code []} 通配括号内的某一个字符<br>
     *
     * @param pattern 正则
     * @return key集合
     */
    public Set<String> keys(String pattern) {
        return template.keys(pattern);
    }

    /**
     * 重命名 key
     *
     * @param key    key
     * @param newKey 新key
     */
    public void rename(String key, String newKey) {
        template.rename(key, newKey);
    }

    /**
     * 将key设置为永不过期
     *
     * @param key key
     * @return {@code true} 成功
     */
    public boolean persist(String key) {
        return Boolean.TRUE.equals(template.persist(key));
    }

    /**
     * 添加 键值存储
     *
     * @param key   key
     * @param value value
     */
    public <T> void set(String key, T value) {
        template.opsForValue().set(key, serialize(value));
    }

    /**
     * 添加键值存储 并设置过期时间，单位秒
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间
     */
    public <T> void set(String key, T value, long timeout) {
        set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 添加键值存储 并设置过期时间
     *
     * @param key      key
     * @param value    value
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> void set(String key, T value, long timeout, final TimeUnit timeUnit) {
        template.opsForValue().set(key, serialize(value), timeout, timeUnit);
    }

    /**
     * 仅当key不存在时设置
     *
     * @param key   key
     * @param value value
     */
    public <T> void setIfAbsent(String key, T value) {
        template.opsForValue().setIfAbsent(key, serialize(value));
    }

    /**
     * 仅当key不存在时设置，并设置过期时间，单位秒
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间
     */
    public <T> void setIfAbsent(String key, T value, long timeout) {
        setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 仅当key不存在时设置，并设置过期时间
     *
     * @param key      key
     * @param value    value
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> void setIfAbsent(String key, T value, long timeout, final TimeUnit timeUnit) {
        template.opsForValue().setIfAbsent(key, serialize(value), timeout, timeUnit);
    }

    /**
     * 批量设置 k-v存储，如key已存在将被覆盖
     *
     * @param values k-v存储
     */
    public <K, V> void multiSet(Map<K, V> values) {
        Map<String, String> data = values.entrySet().stream()
                .map(entry -> Map.entry(serialize(entry.getKey()), serialize(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        template.opsForValue().multiSet(data);
        data.clear();
    }

    /**
     * 批量设置 k-v存储，如key已存在将被覆盖<br>
     * 奇数参数必须为key，偶数参数必须为value<br>
     *
     * @param keysAndValues k-v存储
     */
    public void multiSet(Object... keysAndValues) {
        Map<String, String> data = serializeKeysAndValuesToMap(keysAndValues);
        template.opsForValue().multiSet(data);
        data.clear();
    }

    /**
     * 批量设置键值存储，仅当key不存在时设置
     * ! 注意 有一个key已存在则全部不设置
     *
     * @param map k-v存储
     * @return {@code true} 成功
     */
    public <K, V> boolean multiSetIfAbsent(Map<K, V> map) {
        Map<String, String> data = map.entrySet().stream()
                .map(entry -> Map.entry(serialize(entry.getKey()), serialize(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return Boolean.TRUE.equals(template.opsForValue().multiSetIfAbsent(data));
    }

    /**
     * 批量设置键值存储
     * 奇数参数必须为key，偶数参数必须为value<br>
     * ! 注意 有一个key已存在则全部不设置
     *
     * @param keysAndValues k-v存储
     * @return {@code true} 成功
     */
    public <K, V> boolean multiSetIfAbsent(Object... keysAndValues) {
        Map<String, String> data = serializeKeysAndValuesToMap(keysAndValues);
        Boolean result = template.opsForValue().multiSetIfAbsent(data);
        data.clear();
        return Boolean.TRUE.equals(result);
    }

    /**
     * 将 key 中储存的数字值减少 {@code decrement}
     *
     * @param key       key
     * @param decrement 减少值
     * @return 减少后的值
     */
    public <T> long decrement(String key, long decrement) {
        Long result = template.opsForValue().decrement(key, decrement);
        return result == null ? 0 : result;
    }

    /**
     * 将 key 中储存的数字值减1
     *
     * @param key key
     * @return 减1后的值
     */
    public <T> long decrement(String key) {
        return decrement(key, 1);
    }

    /**
     * 将 key 中储存的数字值增加 {@code increment}
     *
     * @param key       key
     * @param increment 增加值
     * @return 增加后的值
     */
    public <T> long increment(String key, long increment) {
        Long result = template.opsForValue().increment(key, increment);
        return result == null ? 0 : result;
    }

    /**
     * 将 key 中储存的数字值加1
     *
     * @param key key
     * @return 加1后的值
     */
    public <T> long increment(String key) {
        return increment(key, 1);
    }

    /**
     * 获取缓存值
     *
     * @param key key
     * @return 结果
     */
    public String get(String key) {
        return template.opsForValue().get(key);
    }

    /**
     * 获取缓存值并进行反序列化<br>
     * 值类型为list请使用{@link #getList(String, Class)}
     *
     * @param key   key
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> T get(String key, Class<T> clazz) {
        return deserialize(get(key), clazz);
    }

    /**
     * 从缓存中获取值并反序列化为List
     * !注意 区分list存储结构 {@link #range(String, long, long)}
     *
     * @param key   key
     * @param clazz 元素类型
     * @return 结果
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        return JSONUtil.toList(get(key), clazz);
    }

    /**
     * 批量获取值
     *
     * @param keys key集合
     * @return 结果
     */
    public List<String> multiGet(Collection<String> keys) {
        return template.opsForValue().multiGet(keys);
    }

    /**
     * 批量获取值 并进行反序列化
     *
     * @param keys  key集合
     * @param clazz 目标类型
     */
    public <T> List<T> multiGet(Collection<String> keys, Class<T> clazz) {
        return toList(multiGet(keys), clazz);
    }

    /**
     * 如果 key 已经存在并且是一个字符串，指定的 value 追加到该 key 原来值（value）的末尾
     *
     * @param key   key
     * @param value value
     * @return 追加后的长度
     */
    public <T> Integer append(String key, T value) {
        return template.opsForValue().append(key, serialize(value));
    }

    /**
     * 删除 hashMap 中的元素
     *
     * @param key     key
     * @param hashKey hashKey
     * @return 成功删除的数量
     */
    public long remove(String key, String... hashKey) {
        Long result = null;
        if (ArrayUtil.isNotEmpty(hashKey)) {
            result = template.opsForHash().delete(key, (Object[]) hashKey);
        }
        return result != null ? result : 0;
    }

    /**
     * 删除 hashMap 中的元素
     *
     * @param key     key
     * @param hashKey hashKey
     * @return 成功删除的数量
     */
    public long remove(String key, Collection<String> hashKey) {
        return remove(key, hashKey.toArray(String[]::new));
    }

    /**
     * 判断 hashMap 是否存在key
     * !注意区分 {@link #hasKey(String)}
     *
     * @param key     key
     * @param hashKey hashKey
     * @return {@code true} 存在
     */
    public boolean hasKey(String key, String hashKey) {
        return Boolean.TRUE.equals(template.opsForHash().hasKey(key, hashKey));
    }

    /**
     * 从hashMap中获取元素
     *
     * @param key  key
     * @param hKey hashKey
     * @return 结果
     */
    public String get(String key, String hKey) {
        return (String) template.opsForHash().get(key, hKey);
    }

    /**
     * 从hashMap中获取元素 并进行反序列化<br>
     * 值类型为list请使用{@link #getList(String, String, Class)}}
     *
     * @param key   key
     * @param hKey  hashKey
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> T get(String key, String hKey, Class<T> clazz) {
        return deserialize(get(key, hKey), clazz);
    }

    /**
     * 从hashMap中获取值为List的元素，并对List中每个元素进行反序列化
     *
     * @param key   key
     * @param hKey  hashKey
     * @param clazz 元素类型
     * @return 结果
     */
    public <T> List<T> getList(String key, String hKey, Class<T> clazz) {
        return JSONUtil.toList(get(key, hKey), clazz);
    }

    /**
     * 获取hashMap
     *
     * @param key key
     * @return 结果
     */
    public Map<String, String> getAll(String key) {
        HashOperations<String, String, String> operations = template.opsForHash();
        return operations.entries(key);
    }

    /**
     * 获取hashMap并对键值进行反序列化
     *
     * @param key    缓存key
     * @param kClass map key类型
     * @param vClass map value类型
     * @return 结果
     */
    public <K, V> Map<K, V> getAll(String key, Class<K> kClass, Class<V> vClass) {
        return toMap(getAll(key), kClass, vClass);
    }

    /**
     * 获取hashMap并对值进行反序列化
     *
     * @param key   缓存key
     * @param clazz map value类型
     * @return 结果
     */
    public <T> Map<String, T> getAll(String key, Class<T> clazz) {
        return getAll(key, String.class, clazz);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     *
     * @param key       key
     * @param hkey      hashKey
     * @param increment 增量
     */
    public <T> void increment(String key, T hkey, long increment) {
        template.opsForHash().increment(key, serialize(hkey), increment);
    }

    /**
     * 为哈希表 key 中的指定字段的浮点数值加上增量 increment
     *
     * @param key       key
     * @param hkey      hashKey
     * @param increment 增量
     */
    public <T> void increment(String key, T hkey, double increment) {
        template.opsForHash().increment(key, serialize(hkey), increment);
    }

    /**
     * 批量获取 hashMap的value
     *
     * @param key  key
     * @param keys hashKey
     * @return 结果
     */
    public List<String> multiGet(String key, Collection<Object> keys) {
        return template.opsForHash().multiGet(key, keys).stream()
                .filter(Objects::nonNull)
                .map(e -> (String) e).collect(Collectors.toList());
    }

    /**
     * 获取hashMap中多个key的值 并进行反序列化
     *
     * @param key      key
     * @param hashKeys hashKey
     * @param clazz    目标类型
     * @return 结果
     */
    public <T> List<T> multiGet(String key, Collection<Object> hashKeys, Class<T> clazz) {
        return toList(multiGet(key, hashKeys), clazz);
    }

    /**
     * 获取hashMap的大小
     *
     * @param key key
     * @return 结果
     */
    public long size(String key) {
        return template.opsForHash().size(key);
    }

    /**
     * 添加 hash存储
     *
     * @param key     key
     * @param dataMap hashMap
     */
    public <K, V> void putAll(String key, Map<K, V> dataMap) {
        if (CollUtil.isNotEmpty(dataMap)) {
            Map<String, String> stringMap = dataMap.entrySet().stream()
                    .map(entry -> Map.entry(serialize(entry.getKey()), serialize(entry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            template.opsForHash().putAll(key, stringMap);
            stringMap.clear();
        }
    }

    /**
     * 向hashMap中添加元素
     *
     * @param key   key
     * @param hKey  mapKey
     * @param value value
     */
    public <T> void put(String key, String hKey, T value) {
        template.opsForHash().put(key, hKey, serialize(value));
    }

    /**
     * 批量为hashMap添加元素<br>
     * {@code keysAndValues} 长度为奇数时，最后一条将被丢弃
     *
     * @param key           key
     * @param keysAndValues 键值对
     */
    public void putAll(String key, Object... keysAndValues) {
        Map<String, String> data = serializeKeysAndValuesToMap(keysAndValues);
        putAll(key, data);
        data.clear();
    }

    /**
     * 向hashMap中添加元素，仅在不存在的情况下插入
     *
     * @param key   key
     * @param hKey  mapKey
     * @param value value
     * @return 结果
     */
    public <T> boolean putIfAbsent(String key, String hKey, T value) {
        return template.opsForHash().putIfAbsent(key, hKey, serialize(value));
    }

    /**
     * 获取 hashMap 所有 key
     *
     * @param key key
     * @return 结果
     */
    public Set<String> hashKeys(String key) {
        return template.opsForHash().keys(key).stream().map(item -> (String) item).collect(Collectors.toSet());
    }

    /**
     * 获取hashMap所有 key，并进行反序列化
     *
     * @param key   key
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> Set<T> hashKeys(String key, Class<T> clazz) {
        return toSet(hashKeys(key), clazz);
    }

    /**
     * 获取 hashMap所有值
     *
     * @param key key
     * @return 结果
     */
    public Set<String> values(String key) {
        return template.opsForHash().values(key).stream().map(item -> (String) item).collect(Collectors.toSet());
    }

    /**
     * 获取hashMap所有值，并进行反序列化
     *
     * @param key   key
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> Set<T> values(String key, Class<T> clazz) {
        return toSet(values(key), clazz);
    }

    /**
     * 向list尾部 添加元素
     *
     * @param key   key
     * @param value 值
     * @return 结果
     */
    public <T> long rightPush(String key, T value) {
        Long result = template.opsForList().rightPush(key, serialize(value));
        return result != null ? result : 0;
    }

    /**
     * 向list尾部添加集合
     *
     * @param key  key
     * @param list 集合
     * @return 结果
     */
    public <T> long rightPush(String key, List<T> list) {
        Long result = template.opsForList().rightPushAll(key, list.stream()
                .map(RedisService::serialize).collect(Collectors.toList()));
        return result != null ? result : 0;
    }

    /**
     * 向list尾部添加元素
     *
     * @param key   key
     * @param value 值
     * @return 结果
     */
    public <T> long leftPush(String key, T value) {
        Long result = template.opsForList().leftPush(key, serialize(value));
        return result != null ? result : 0;
    }

    /**
     * 向list头部添加集合
     *
     * @param key  key
     * @param list 集合
     * @return 结果
     */
    public <T> long leftPush(String key, List<T> list) {
        Long result = template.opsForList().leftPushAll(key, list.stream()
                .map(RedisService::serialize).collect(Collectors.toList()));
        return result != null ? result : 0;
    }

    /**
     * 返回list第一个元素并将其删除
     *
     * @param key key
     * @return 结果
     */
    public String leftPop(String key) {
        return template.opsForList().leftPop(key);
    }

    /**
     * 返回list第一个元素并将其删除 并对值进行反序列化
     *
     * @param key   key
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> T leftPop(String key, Class<T> clazz) {
        return deserialize(leftPop(key), clazz);
    }

    /**
     * 返回list最后一个元素并将其删除
     *
     * @param key key
     * @return 结果
     */
    public String rightPop(String key) {
        return template.opsForList().rightPop(key);
    }

    /**
     * 返回list最后一个元素并将其删除 并对值进行反序列化
     *
     * @param key   key
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> T rightPop(String key, Class<T> clazz) {
        return deserialize(rightPop(key), clazz);
    }

    /**
     * 从list中获取元素
     *
     * @param key   key
     * @param begin 开始位置
     * @param end   结束位置
     * @return 结果
     */
    public List<String> range(String key, long begin, long end) {
        return template.opsForList().range(key, begin, end);
    }

    /**
     * 从list中获取元素并进行反序列化
     *
     * @param key   key
     * @param begin 开始位置
     * @param end   结束位置
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> List<T> range(String key, long begin, long end, Class<T> clazz) {
        return toList(range(key, begin, end), clazz);
    }

    /**
     * 向ZSet添加元素
     *
     * @param key   key
     * @param value 值
     * @param score 分数
     * @return 结果
     */
    public <T> boolean zAdd(String key, T value, double score) {
        return Boolean.TRUE.equals(template.opsForZSet().add(key, serialize(value), score));
    }

    /**
     * 向ZSet添加元素 仅不存在时添加
     *
     * @param key   key
     * @param value 值
     * @param score 分数
     * @return 结果
     */
    public <T> boolean zAddIfAbsent(String key, T value, double score) {
        return Boolean.TRUE.equals(template.opsForZSet().addIfAbsent(key, serialize(value), score));
    }

    /**
     * 向ZSet添加元素
     *
     * @param key    key
     * @param tuples 值
     * @return 结果
     */
    public <T> long zAdd(String key, Set<ZSetOperations.TypedTuple<T>> tuples) {
        Set<ZSetOperations.TypedTuple<String>> data = tuples.stream()
                .map(item -> ZSetOperations.TypedTuple.of(serialize(item.getValue()), item.getScore()))
                .collect(Collectors.toSet());
        Long result = template.opsForZSet().add(key, data);
        return result != null ? result : 0;
    }

    /**
     * 向ZSet添加元素 仅不存在时添加
     *
     * @param key    key
     * @param tuples 值
     * @return 结果
     */
    public <T> long zAddIfAbsent(String key, Set<ZSetOperations.TypedTuple<T>> tuples) {
        Set<ZSetOperations.TypedTuple<String>> data = tuples.stream()
                .map(item -> ZSetOperations.TypedTuple.of(serialize(item.getValue()), item.getScore()))
                .collect(Collectors.toSet());
        Long result = template.opsForZSet().addIfAbsent(key, data);
        return result != null ? result : 0;
    }

    /**
     * 从ZSet中获取元素
     *
     * @param key   key
     * @param begin 开始位置
     * @param end   结束位置
     * @return 结果
     */
    public Set<String> zRange(String key, long begin, long end) {
        return template.opsForZSet().range(key, begin, end);
    }

    /**
     * 从ZSet中获取元素并进行序列化
     *
     * @param key   key
     * @param begin 开始位置
     * @param end   结束位置
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> Set<T> zRange(String key, long begin, long end, Class<T> clazz) {
        return toSet(zRange(key, begin, end), clazz);
    }

    /**
     * 按分数获取ZSet内元素
     *
     * @param key key
     * @param min 最小分数
     * @param max 最大分数
     * @return 结果
     */
    public Set<String> zRangeByScore(String key, long min, long max) {
        return template.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 按分数获取ZSet内元素并进行反序列化
     *
     * @param key   key
     * @param min   最小分数
     * @param max   最大分数
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> Set<T> zRangeByScore(String key, long min, long max, Class<T> clazz) {
        return toSet(zRangeByScore(key, min, max), clazz);
    }

    /**
     * 按照lex条件获取ZSet元素
     *
     * @param key   key
     * @param range 条件
     * @param max   最大值
     * @return 结果
     */
    public Set<String> zRangeByLex(String key, Range<String> range, Limit max) {
        return template.opsForZSet().rangeByLex(key, range, max);
    }

    /**
     * 按照lex条件获取ZSet元素并进行反序列化
     *
     * @param key   key
     * @param range 条件
     * @param limit 最大值
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> Set<T> zRangeByLex(String key, Range<String> range, Limit limit, Class<T> clazz) {
        return zRangeByLex(key, range, limit).stream().map(e -> deserialize(e, clazz)).collect(Collectors.toSet());
    }

    /**
     * 向set添加元素
     *
     * @param key   key
     * @param value 值
     * @return 结果
     */
    public <T> long add(String key, T value) {
        Long result = template.opsForSet().add(key, serialize(value));
        return result != null ? result : 0;
    }

    /**
     * 向set添加集合
     *
     * @param key  key
     * @param list 集合
     * @return 结果
     */
    public <T> long add(String key, Collection<T> list) {
        Long result = template.opsForSet().add(key, list.stream().map(RedisService::serialize).toArray(String[]::new));
        return result != null ? result : 0;
    }

    /**
     * 获取set中所有元素
     *
     * @param key key
     * @return 结果
     */
    public Set<String> members(String key) {
        return template.opsForSet().members(key);
    }

    /**
     * 获取set中所有元素并进行反序列化
     *
     * @param key   key
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> Set<T> members(String key, Class<T> clazz) {
        return members(key).stream().map(e -> deserialize(e, clazz)).collect(Collectors.toSet());
    }

    /**
     * 随机获取不重复的指定数量的元素
     *
     * @param key   key
     * @param count 数量
     * @return 结果
     */
    public Set<String> distRandom(String key, long count) {
        return template.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 随机获取不重复的指定数量的元素，并进行反序列化
     *
     * @param key   key
     * @param count 数量
     * @return 结果
     */
    public <T> Set<T> distRandom(String key, long count, Class<T> clazz) {
        return toSet(distRandom(key, count), clazz);
    }

    /**
     * 获得 {@code key} 和 {@code otherKey} 的差集
     *
     * @param key      key
     * @param otherKey otherKey
     * @return 结果
     */
    public Set<String> diff(String key, String otherKey) {
        return template.opsForSet().difference(key, otherKey);
    }

    /**
     * 获得 {@code key} 和 {@code otherKey} 的差集，并进行反序列化
     *
     * @param key      key
     * @param otherKey otherKey
     * @param clazz    目标类型
     * @return 结果
     */
    public <T> Set<T> diff(String key, String otherKey, Class<T> clazz) {
        return toSet(diff(key, otherKey), clazz);
    }

    /**
     * 获得多个key之间的差集
     *
     * @param keys keys
     * @return 结果
     */
    public Set<String> diff(Collection<String> keys) {
        return template.opsForSet().difference(keys);
    }

    /**
     * 获得多个key之间的差集，并进行反序列化
     *
     * @param keys  keys
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> Set<T> diff(Collection<String> keys, Class<T> clazz) {
        return toSet(diff(keys), clazz);
    }

    /**
     * 获得 {@code key} 和 多个key的差集
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @return 结果
     */
    public Set<String> diff(String key, Collection<String> otherKeys) {
        return template.opsForSet().difference(key, otherKeys);
    }

    /**
     * 获得 {@code key} 和 多个key的差集，并进行反序列化
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @return 结果
     */
    public <T> Set<T> diff(String key, Collection<String> otherKeys, Class<T> clazz) {
        return toSet(diff(key, otherKeys), clazz);
    }

    /**
     * 将 {@code key} 和 {@code otherKeys} 的差集存储到 {@code destKey} 中
     *
     * @param key      key
     * @param otherKey otherKey
     * @param destKey  destKey
     * @return 结果
     */
    public long diffAndStore(String key, String otherKey, String destKey) {
        Long result = template.opsForSet().differenceAndStore(key, otherKey, destKey);
        return result != null ? result : 0;
    }

    /**
     * 将 {@code keys} 之间的差集存储到 {@code destKey} 中
     *
     * @param keys    keys
     * @param destKey destKey
     * @return 结果
     */
    public long diffAndStore(Collection<String> keys, String destKey) {
        Long result = template.opsForSet().differenceAndStore(keys, destKey);
        return result != null ? result : 0;
    }

    /**
     * 将 {@code key} 和 {@code otherKeys} 的差集存储到 {@code destKey} 中
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @param destKey   destKey
     * @return 结果
     */
    public long diffAndStore(String key, Collection<String> otherKeys, String destKey) {
        Long result = template.opsForSet().differenceAndStore(key, otherKeys, destKey);
        return result != null ? result : 0;
    }

    /**
     * 获得 {@code key} 和 {@code otherKey} 的交集
     *
     * @param key      key
     * @param otherKey otherKeys
     * @return 结果
     */
    public Set<String> intersect(String key, String otherKey) {
        return template.opsForSet().intersect(key, otherKey);
    }

    /**
     * 获得 {@code key} 和 {@code otherKey} 的交集，并进行反序列化
     *
     * @param key      key
     * @param otherKey otherKeys
     * @param clazz    目标类型
     * @return 结果
     */
    public <T> Set<T> intersect(String key, String otherKey, Class<T> clazz) {
        return toSet(intersect(key, otherKey), clazz);
    }

    /**
     * 获得多个key之间的交集
     *
     * @param keys keys
     * @return 结果
     */
    public Set<String> intersect(Collection<String> keys) {
        return template.opsForSet().intersect(keys);
    }

    /**
     * 获得多个key之间的交集，并进行反序列化
     *
     * @param keys  keys
     * @param clazz 目标类型
     * @return 结果
     */
    public <T> Set<T> intersect(Collection<String> keys, Class<T> clazz) {
        return toSet(intersect(keys), clazz);
    }

    /**
     * 获得 {@code key} 和 多个key的交集
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @return 结果
     */
    public Set<String> intersect(String key, Collection<String> otherKeys) {
        return template.opsForSet().intersect(key, otherKeys);
    }

    /**
     * 获得 {@code key} 和 多个key的交集，并进行反序列化
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @return 结果
     */
    public <T> Set<T> intersect(String key, Collection<String> otherKeys, Class<T> clazz) {
        return toSet(intersect(key, otherKeys), clazz);
    }

    /**
     * 将 {@code key} 和 {@code otherKey} 的交集存储到 {@code destKey} 中
     *
     * @param key      key
     * @param otherKey otherKey
     * @param destKey  destKey
     * @return 结果
     */
    public long intersectAndStore(String key, String otherKey, String destKey) {
        Long result = template.opsForSet().intersectAndStore(key, otherKey, destKey);
        return result != null ? result : 0;
    }

    /**
     * 将 {@code keys} 之间的交集存储到 {@code destKey} 中
     *
     * @param keys    keys
     * @param destKey destKey
     * @return 结果
     */
    public long intersectAndStore(Collection<String> keys, String destKey) {
        Long result = template.opsForSet().intersectAndStore(keys, destKey);
        return result != null ? result : 0;
    }

    /**
     * 将 {@code key} 和 {@code otherKeys} 的交集存储到 {@code destKey} 中
     *
     * @param key       key
     * @param otherKeys otherKeys
     * @param destKey   destKey
     * @return 结果
     */
    public long intersectAndStore(String key, Collection<String> otherKeys, String destKey) {
        Long result = template.opsForSet().intersectAndStore(key, otherKeys, destKey);
        return result != null ? result : 0;
    }

    /**
     * 将对象序列化为json串，不会处理 {@link CharSequence}、{@link Number}、{@link Boolean}
     *
     * @param o 对象
     * @return json串
     */
    public static String serialize(Object o) {
        Assert.notNull(o, "序列化对象不能为null");
        if (o instanceof CharSequence || o instanceof Number || o instanceof Boolean) {
            return o.toString();
        }
        return JSONUtil.toJsonStr(o);
    }

    /**
     * 把字符串 反序列化为指定对象
     *
     * @param str   字符串
     * @param clazz 目标类型
     * @return 结果
     */
    public static <T> T deserialize(String str, Class<T> clazz) {
        if (JSONUtil.isTypeJSONArray(str) && clazz.isAssignableFrom(Iterator.class)) {
            return JSONUtil.parseArray(str).toBean(clazz);
        }
        if (JSONUtil.isTypeJSONObject(str)) {
            return JSONUtil.toBean(str, clazz);
        }
        return Convert.convert(clazz, str);
    }

    /**
     * 对字符串集合进行批量反序列化
     *
     * @param data  字符串集合
     * @param clazz 目标类型
     * @return 结果
     */
    public static <T> List<T> toList(Collection<String> data, Class<T> clazz) {
        return data.stream().filter(Objects::nonNull).map(e -> deserialize(e, clazz)).collect(Collectors.toList());
    }

    /**
     * 对字符串集合进行批量反序列化
     *
     * @param data  字符串集合
     * @param clazz 目标类型
     * @return 结果
     */
    public static <T> Set<T> toSet(Collection<String> data, Class<T> clazz) {
        return data.stream().filter(Objects::nonNull).map(e -> deserialize(e, clazz)).collect(Collectors.toSet());
    }

    /**
     * 对 Map<String, String> data 进行类型转换
     *
     * @param data   Map<String, String> data
     * @param kClass key类型
     * @param vClass value类型
     * @return 结果
     */
    public static <K, V> Map<K, V> toMap(Map<String, String> data, Class<K> kClass, Class<V> vClass) {
        return data.entrySet().stream()
                .map(entry -> Map.entry(deserialize(entry.getKey(), kClass), deserialize(entry.getValue(), vClass)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 以可变参数接收Map，进行序列化，奇数位为key，偶数位为value<br>
     * !多出的key会被忽略
     *
     * @param keysAndValues keysAndValues
     * @return Map
     */
    private static Map<String, String> serializeKeysAndValuesToMap(Object... keysAndValues) {
        Map<String, String> data = new HashMap<>();
        String keyStr = null;
        for (int i = 0; i < keysAndValues.length; ++i) {
            if (i % 2 == 0) {
                keyStr = serialize(keysAndValues[i]);
            } else {
                data.put(keyStr, serialize(keysAndValues[i]));
            }
        }
        return data;
    }
}
