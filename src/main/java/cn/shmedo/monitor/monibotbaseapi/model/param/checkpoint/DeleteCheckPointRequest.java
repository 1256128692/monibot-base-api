package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteCheckPointRequest extends AbsDeleteCheckPoint {

    @Override
    public ResultWrapper<?> validate() {
        super.validate();
        Assert.isTrue(super.validatePointStatus(),
                () -> new InvalidParameterException("所选的巡检点有正在执行的巡检任务，无法删除，请确认无正在执行的巡检任务再做删除。"));
        return null;
    }

    @Override
    public String toString() {
        return "DeleteCheckPointRequest{" +
                "idList=" + getIdList() +
                '}';
    }
}