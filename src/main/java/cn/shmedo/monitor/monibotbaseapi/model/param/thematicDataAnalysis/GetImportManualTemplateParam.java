package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.MonitorTypeFieldV2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 10:42
 */
@Data
public class GetImportManualTemplateParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空!")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotNull(message = "监测类型D不能为空!")
    @Positive(message = "监测类型不能小于1")
    private Integer monitorType;
    @JsonIgnore
    Map<FieldClass, List<MonitorTypeFieldV2>> classFieldMap;

    @Override
    public ResultWrapper<?> validate() {
        classFieldMap = ContextHolder.getBean(TbMonitorTypeFieldMapper.class).queryByMonitorTypesV2(List.of(monitorType), true)
                .stream().collect(Collectors.groupingBy(u -> FieldClass.codeOf(u.getFieldClass())));
        if (CollUtil.isEmpty(classFieldMap)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在或未设置子类型,无法生成模板!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
