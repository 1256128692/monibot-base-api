package cn.shmedo.monitor.monibotbaseapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Created  on 2015/5/22.
 * 返回默认的JSON ObjectMapper
 * 规范：Pascal命名规则，时间序列化成：2018-01-01 12:01:59，时区东八区
 *
 * @author Liudongdong
 */
@Provider
public class DefaultJsonConfig implements ContextResolver<ObjectMapper> {
    private ObjectMapper om = new ObjectMapper();

    public DefaultJsonConfig() {
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.registerModule(new JavaTimeModule());
        //要注意格式字母是区分大小写的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        om.setDateFormat(sdf);
        om.getDeserializationConfig().with(sdf);
        om.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return om;
    }
}
