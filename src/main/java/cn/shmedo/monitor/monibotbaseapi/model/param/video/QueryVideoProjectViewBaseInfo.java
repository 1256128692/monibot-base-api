package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 11:49
 */
@Data
public class QueryVideoProjectViewBaseInfo implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @Range(max = 2, message = "视频设备状态枚举 0.全部 1.仅在线 2.仅离线")
    private Integer status;
    private String deviceSerial;
    private Integer deviceChannel;

    @Override
    public ResultWrapper validate() {
        if (!ContextHolder.getBean(TbProjectInfoMapper.class).exists(new LambdaQueryWrapper<TbProjectInfo>().eq(TbProjectInfo::getID, projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
