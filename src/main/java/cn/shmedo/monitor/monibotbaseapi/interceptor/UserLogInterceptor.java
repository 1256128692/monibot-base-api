package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbUserLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志记录
 */
@Aspect
@Component
@Order(InterceptorOrder.LOG)
public class UserLogInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(UserLogInterceptor.class);
    private static final int MAX_ARGS_LENGTH = 1800;
    private static final String NO_IP = "none";

    @Around(value = "@annotation(logParam)")
    public Object aroundResourceMethod(ProceedingJoinPoint joinPoint, LogParam logParam) {
        try {
            Object result = joinPoint.proceed();
            Object[] args = joinPoint.getArgs();
            String methodName = joinPoint.getSignature().getName();
            CurrentSubject currentSubject = CurrentSubjectHolder.getCurrentSubject();
            doLog(args, methodName, currentSubject, logParam);
            return result;
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new RuntimeException(throwable);
            }

        }
    }

    private void doLog(Object[] args, String methodName, CurrentSubject currentSubject, LogParam logParam) {
        if (currentSubject == null) {
            return;
        }
        if (currentSubject.getSubjectID() < 0) {
            // 非用户操作不记录
            return;
        }
        try {

            String argsString = formatArgsString(args);
            TbUserLog userLog = new TbUserLog();
            // 处理非用户操作时公司id为null的情况
            userLog.setCompanyID(currentSubject.getCompanyID() == null ? -1 : currentSubject.getCompanyID());
            userLog.setUserID(currentSubject.getSubjectID());
            userLog.setUserName(currentSubject.getSubjectName());
            userLog.setOperationDate(LocalDateTime.now());
            userLog.setOperationIP(NO_IP);
            userLog.setModuleName(logParam.moduleName());
            userLog.setOperationName(logParam.operationName());
            userLog.setOperationProperty(logParam.operationProperty().toString());
            userLog.setOperationPath(methodName);
            userLog.setOperationParams(argsString);
            ContextHolder.getBean(TaskExecutor.class).execute(() -> {
                try {
                    writeLogToDb(userLog);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            });
        } catch (Exception exOut) {
            logger.error(exOut.getMessage(), exOut);
        }
    }

    /**
     * @param userLog
     */
    private void writeLogToDb(TbUserLog userLog) {

        TbUserLogMapper tbUserLogMapper = ContextHolder.getBean(TbUserLogMapper.class);
        if (userLog == null) {
            return;
        }
        tbUserLogMapper.insertSelective(userLog);

    }

    private String formatArgsString(Object[] args) {
        if (args == null || args.length <= 0) {
            return "no args";
        }
        StringBuilder builder = new StringBuilder();
        Arrays.stream(args).forEach(arg -> {
            builder.append(arg.toString());
        });
        if (builder.length() > MAX_ARGS_LENGTH) {
            builder.delete(MAX_ARGS_LENGTH, builder.length());
        }
        return builder.toString();
    }
}
