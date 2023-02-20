package cn.shmedo.monitor.monibotbaseapi.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class CommonBeans {
    public static final String PASCAL_OBJECT_MAPPER = "pascalObjectMapper";
    public static final String CAMEL_OBJECT_MAPPER = "camelObjectMapper";

    @Bean(PASCAL_OBJECT_MAPPER)
    public ObjectMapper pascalObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //要注意格式字母是区分大小写的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        objectMapper.setDateFormat(sdf);
        objectMapper.getDeserializationConfig().with(sdf);
        return objectMapper;
    }

    @Primary
    @Bean(CAMEL_OBJECT_MAPPER)
    public ObjectMapper camelObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //要注意格式字母是区分大小写的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        objectMapper.setDateFormat(sdf);
        objectMapper.getDeserializationConfig().with(sdf);
        return objectMapper;
    }
}
