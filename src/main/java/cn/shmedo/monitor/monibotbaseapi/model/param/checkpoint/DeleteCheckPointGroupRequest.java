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
public class DeleteCheckPointGroupRequest extends AbsDeleteCheckPointGroup {

    @Override
    public ResultWrapper<?> validate() {
        super.validate();
        Assert.isTrue(super.validateGroupStatus(),
                () -> new InvalidParameterException("所选的巡检组中包含有正在执行巡检任务的巡检点，请确认任务执行结束再做删除。"));
        return null;
    }

    @Override
    public String toString() {
        return "DeleteCheckPointGroupRequest{" +
                "idList=" + getIdList() +
                '}';
    }
}