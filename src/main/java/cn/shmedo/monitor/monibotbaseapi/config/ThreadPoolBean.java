package cn.shmedo.monitor.monibotbaseapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 * Created on 18/4/17.
 *
 * @author Liudongdong
 */
@Configuration
public class ThreadPoolBean {
    private static final int MAX_THREAD = 10;
    private static final int MAX_QUEUE_CAPACITY = 100000;

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(MAX_THREAD);
        taskExecutor.setQueueCapacity(MAX_QUEUE_CAPACITY);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
