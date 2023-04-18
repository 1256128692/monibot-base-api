package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 获取试运行参数 请求体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class QueryTryingParamRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 监测类型
     */
    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    /**
     * 监测类型模板ID
     */
    @NotNull(message = "监测类型模板ID不能为空")
    private Integer templateID;

    /**
     * 监测类型模板
     */
    @JsonIgnore
    private TbMonitorTypeTemplate monitorTypeTemplate;

    /**
     * 监测类型字段
     */
    @JsonIgnore
    private List<TbMonitorTypeField> typeFields;

    @Override
    public ResultWrapper<?> validate() {
        TbMonitorTypeTemplateMapper monitorTypeTemplateMapper = SpringUtil.getBean(TbMonitorTypeTemplateMapper.class);
        this.monitorTypeTemplate = monitorTypeTemplateMapper.selectById(this.templateID);
        Assert.notNull(monitorTypeTemplate, "监测类型模板不存在");

        TbMonitorTypeFieldMapper monitorTypeFieldMapper = SpringUtil.getBean(TbMonitorTypeFieldMapper.class);
        this.typeFields = monitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                .eq(TbMonitorTypeField::getMonitorType, monitorType)
                .in(TbMonitorTypeField::getFieldClass, FieldClass.BASIC.getCode(),
                        FieldClass.EXTEND.getCode()));
        Assert.notEmpty(typeFields, "监测类型不存在");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    