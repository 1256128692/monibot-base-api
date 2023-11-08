package cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddDataEventParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    @NotBlank(message = "大事件名称Name不能为空")
    private String name;
    @NotNull(message = "频率frequency不能为空")
    private Integer frequency;
    @NotBlank(message = "时间范围不能为空")
    private String timeRange;
    private String exValue;
    @NotEmpty
    private List<@NotNull Integer> monitorItemIDList;

    public static TbDataEvent toNewVo(AddDataEventParam pa, Integer subjectID) {
        DateTime date = DateUtil.date();
        TbDataEvent vo = new TbDataEvent();
        vo.setProjectID(pa.getProjectID());
        vo.setName(pa.getName());
        vo.setTimeRange(pa.getTimeRange());
        vo.setFrequency(pa.getFrequency());
        vo.setExValue(pa.getExValue());
        vo.setCreateUserID(subjectID);
        vo.setUpdateUserID(subjectID);
        vo.setCreateTime(date);
        vo.setUpdateTime(date);
        return vo;
    }

    @Override
    public ResultWrapper validate() {
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
