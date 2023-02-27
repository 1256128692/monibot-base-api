package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 统一处理接口参数不匹配问题
 * @program: mdcs-api
 * @author: gaoxu
 * @create: 2022-01-27 14:37
 **/


@RestControllerAdvice
public class ExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler({HttpMessageNotReadableException.class, ConstraintViolationException.class} )
    public Object handleException(Exception e){
        LOGGER.warn(e.getMessage());
       if (e instanceof HttpMessageNotReadableException){
           return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,
                   "参数不匹配 ※※※※※※※※※ Exception: " + e.getClass().getName() + " ※※※※※※※※※ " +
                           "Message: " + e.getMessage());
       } else {
           return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,
                   "参数校验不通过 ※※※※※※※※※ Exception: " + e.getClass().getName() + " ※※※※※※※※※ " +
                           "Message: " + e.getMessage());
       }
    }

}
