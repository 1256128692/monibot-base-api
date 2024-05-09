package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.MonitorTypeTemplateAndTemplateDataSource;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2023/4/10
 */
@Data
public class BaseConfigRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 监测类型模板ID
     */
    private Integer templateID;

    /**
     * 模板数据源标识
     */
    private String templateDataSourceToken;

    /**
     * 监测类型
     */
    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    @Override
    public ResultWrapper<?> validate() {
        if (Objects.isNull(templateID) && StringUtils.isBlank(templateDataSourceToken))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型模板ID，模板数据源标识不能同时为空");
        if (Objects.isNull(templateID)) {
            TbMonitorTypeTemplateMapper templateMapper = ContextHolder.getBean(TbMonitorTypeTemplateMapper.class);
            List<MonitorTypeTemplateAndTemplateDataSource> list = templateMapper.selectMonitorTypeTemplateList(monitorType);
            list = list.stream().filter(item ->
                    Arrays.asList(item.getTemplateDataSourceToken().split(",")).contains(templateDataSourceToken)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(list))
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型模板不存在");
            if (list.size() > 1)
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型模板存在多个");
            templateID = list.get(0).getMonitorTypeTemplateID();
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    