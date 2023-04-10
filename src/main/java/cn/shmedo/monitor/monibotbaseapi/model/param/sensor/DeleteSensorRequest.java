package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 删除传感器参数
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class DeleteSensorRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 传感器ID列表
     */
    @NotEmpty(message = "传感器ID列表不能为空")
    private List<Integer> sensorIDList;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    @Override
    public ResultWrapper<?> validate() {
        TbSensorMapper sensorMapper = SpringUtil.getBean(TbSensorMapper.class);
        Long count = sensorMapper.selectCount(new LambdaQueryWrapper<>(new TbSensor())
                .in(TbSensor::getID, sensorIDList)
                .eq(TbSensor::getProjectID, projectID));
        Assert.isTrue(count.equals((long) sensorIDList.size()), "无法删除不属于本项目的传感器");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    