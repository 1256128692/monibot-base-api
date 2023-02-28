package cn.shmedo.monitor.monibotbaseapi.interceptor;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理，拦截常见异常返回相应的错误码和消息，便于排查问题<br/>
 * 自定义异常 {@link CustomBaseException}、{@link InvalidParameterException} 和 {@code validation} 产生的参数校验异常<br/>
 * 不会被记录到日志中，其他异常会被记录到日志中
 *
 * @author Chengfs on 2022/11/21
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultWrapper<?> handleException(HttpServletRequest request, Exception e) {

        //自定义异常
        if (e instanceof CustomBaseException ex) {
            return ResultWrapper.withCode(ResultCode.valueOfCode(ex.errCode()), e.getMessage());
        }

        //自定义无效参数异常
        if (e instanceof InvalidParameterException ex) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, ex.getMessage());
        }

        //参数校验异常
        if (e instanceof ConstraintViolationException ex) {
            String msg = ex.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage).collect(Collectors.joining(StrUtil.COMMA));
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, msg);
        }

        log.error("{}请求发生异常, 路径: {}，参数: {}", request.getMethod(), request.getRequestURI(),
                JSONUtil.toJsonStr(request.getParameterMap()));
        log.error(ExceptionUtil.stacktraceToString(e));

        //参数绑定异常，通常由错误使用 {@code validation} 注解引起
        if (e instanceof BindException ex) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "参数绑定异常");
        }

        //http消息转换异常，通常由错误的参数格式引起
        if (e instanceof HttpMessageConversionException) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数格式错误");
        }

        //空指针异常
        if (e instanceof NullPointerException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "空指针异常");
        }

        //非法参数异常
        if (e instanceof IllegalArgumentException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION,
                    StrUtil.isBlank(e.getMessage()) ? "非法参数异常" : e.getMessage());
        }

        //越界异常
        if (e instanceof IndexOutOfBoundsException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "索引越界异常");
        }

        //反射异常
        if (e instanceof ReflectiveOperationException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "反射异常");
        }

        //请求方式异常
        if (e instanceof HttpRequestMethodNotSupportedException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "请求方式错误");
        }

        //数据库异常
        if (e instanceof DataAccessException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "数据库异常");
        }

        //其他异常
        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "服务端异常");
    }
}

    
    