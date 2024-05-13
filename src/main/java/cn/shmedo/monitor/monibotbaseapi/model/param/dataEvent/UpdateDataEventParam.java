package cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataEventMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataEvent;
import cn.shmedo.monitor.monibotbaseapi.model.enums.FrequencyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateDataEventParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    @NotNull(message = "大事记id不能为空")
    private Integer id;
    @NotBlank(message = "大事记名称name不能为空")
    private String name;
    @NotNull(message = "频率frequency不能为空")
    private Integer frequency;
    @NotBlank(message = "时间范围不能为空")
    private String timeRange;
    private String exValue;
    @NotEmpty
    private List<@NotNull Integer> monitorItemIDList;

    public static TbDataEvent toNewVo(UpdateDataEventParam pa, Integer subjectID) {
        DateTime date = DateUtil.date();
        TbDataEvent vo = new TbDataEvent();
        vo.setId(pa.getId());
        vo.setProjectID(pa.getProjectID());
        vo.setName(pa.getName());
        vo.setTimeRange(pa.getTimeRange());
        vo.setFrequency(pa.getFrequency());
        vo.setExValue(pa.getExValue());
        vo.setUpdateUserID(subjectID);
        vo.setUpdateTime(date);
        return vo;
    }

    @Override
    public ResultWrapper validate() {
        if (!FrequencyEnum.isValid(frequency)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数:frequency字段值是非法频次类型");
        }
        TbDataEventMapper tbDataEventMapper = ContextHolder.getBean(TbDataEventMapper.class);
        if (tbDataEventMapper.selectCountByName(projectID, name, id) != 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "名称已存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {

        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
