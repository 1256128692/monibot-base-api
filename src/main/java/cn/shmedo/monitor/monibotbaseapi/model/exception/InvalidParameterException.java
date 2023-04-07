package cn.shmedo.monitor.monibotbaseapi.model.exception;


import cn.shmedo.iot.entity.exception.BaseException;
import cn.shmedo.monitor.monibotbaseapi.model.base.ErrCode;

/**
 * Created on 18/1/23
 *
 * @author Liudongdong
 */
public class InvalidParameterException extends BaseException {

    public InvalidParameterException() {
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int errCode() {
        return ErrCode.ERR_INVALID_PARAMETER;
    }
}
