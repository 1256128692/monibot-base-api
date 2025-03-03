package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.BaseException;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


/**
 * 统一处理接口参数不匹配问题
 *
 * @program: mdcs-api
 * @author: gaoxu
 * @create: 2022-01-27 14:37
 **/

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    /**
     * 自定义异常 处理器
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link BaseException}
     * @return {@link ResultWrapper}
     */
    @ResponseBody
    @ExceptionHandler(BaseException.class)
    public ResultWrapper<?> handleBaseException(HttpServletRequest request, BaseException e) {
        //自定义异常
        if (e instanceof CustomBaseException ex) {
            return ResultWrapper.withCode(ResultCode.valueOfCode(ex.errCode()), e.getMessage());
        }

        //自定义无效参数异常
        if (e instanceof InvalidParameterException ex) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, ex.getMessage());
        }
        return generalExceptionHandle(request, e);
    }

    /**
     * 参数异常 处理器
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link ValidationException}
     * @return {@link ResultWrapper}
     */
    @ResponseBody
    @ExceptionHandler({ValidationException.class, BindException.class, HttpMessageConversionException.class})
    public ResultWrapper<?> handleValidationException(HttpServletRequest request, Exception e) {
        //参数校验异常
        if (e instanceof ConstraintViolationException ex) {
//            String msg = ex.getConstraintViolations().stream()
//                    .map(ConstraintViolation::getMessage).collect(Collectors.joining(StrUtil.COMMA));
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数校验不通过:******"  + e.getMessage());
        }

        //参数绑定异常
        if (e instanceof BindException ex) {
            String msg = ex.getBindingResult().getAllErrors().stream()
                    .map(item -> {
                        String fieldName = item instanceof FieldError error ? error.getField() : item.getObjectName();
                        return fieldName + StrUtil.COLON + StrUtil.SPACE + item.getDefaultMessage();
                    }).collect(Collectors.joining(StrUtil.COMMA));
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,
                    StrUtil.isEmpty(msg) ? "参数校验不通过: " + e.getMessage() : msg);

        }

        //http消息转换异常，通常由错误的参数格式引起
        if (e instanceof HttpMessageConversionException) {
            outputLog(request, e);
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数格式错误");
        }

        //约束声明异常 通常由 validation 注解和参数类型不匹配引起
        if (e instanceof ConstraintDeclarationException ex) {
            outputLog(request, e);
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "约束声明异常");
        }
        return generalExceptionHandle(request, e);
    }

    /**
     * 其他异常处理器
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link Exception}
     * @return {@link ResultWrapper}
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultWrapper<?> handleException(HttpServletRequest request, Exception e) {
        outputLog(request, e);

        //空指针异常
        if (e instanceof NullPointerException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "空指针异常");
        }

        //非法参数异常
        if (e instanceof IllegalArgumentException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION,
                    StrUtil.isBlank(e.getMessage()) ? "非法参数异常" : e.getMessage());
        }

        //越界异常，通常由集合越界操作引起
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

        //请求格式异常 通常由错误的 Content-Type 引起
        if (e instanceof HttpMediaTypeException ex) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "请求格式错误");
        }

        //数据库异常
        if (e instanceof DataAccessException) {
            return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "数据库异常");
        }

        //其他异常
        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "服务端异常");
    }

    /**
     * 通用异常处理 输出错误日志并返回 {@link ResultCode#SERVER_EXCEPTION}
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link Exception}
     * @return {@link ResultWrapper}
     */
    private static ResultWrapper<?> generalExceptionHandle(HttpServletRequest request, Exception e) {
        outputLog(request, e);
        return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "服务端发生错误");
    }

    /**
     * 输出详细的异常日志<br/>
     * 如请求参数已经被读取，此处将无法获得
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link Exception}
     */
    private static void outputLog(HttpServletRequest request, Exception e) {
        log.error("{} {} => {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        log.error(ExceptionUtil.stacktraceToString(e));
    }

}
