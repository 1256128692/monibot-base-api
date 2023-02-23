package cn.shmedo.monitor.monibotbaseapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.shmedo.monitor.monibotbaseapi.dal.mapper")
public class MonibotBaseApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonibotBaseApiApplication.class, args);
    }

}
