package cn.shmedo.monitor.monibotbaseapi.config;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbUserLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务
 * @Author cyf
 * @Date 2023/3/9 10:30
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller
 * @ClassName: UserLogController
 * @Description:
 * @Version 1.0
 */
@Slf4j
@Configuration
@EnableScheduling
public class ScheduledTask {

    private TbUserLogMapper tbUserLogMapper;

    @Autowired
    public ScheduledTask(TbUserLogMapper tbUserLogMapper) {
        this.tbUserLogMapper = tbUserLogMapper;
    }
    /**
     * 清理三十天前用户操作记录日志
     * 每天0点执行
     */
    @Scheduled(cron = "0 0 0 * * *")
    private void cleanUserLog(){
        log.info("---------清理日志定时任务开始执行---------"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
        tbUserLogMapper.cleanUserLog();
        log.info("---------清理日志定时任务执行成功---------"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
}
