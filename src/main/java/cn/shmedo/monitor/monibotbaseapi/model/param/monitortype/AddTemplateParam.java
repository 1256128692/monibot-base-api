package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CalType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DatasourceType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-29 16:51
 **/
@Data
public class AddTemplateParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Boolean defaultTemplate;
    @NotNull
    private Integer monitorType;
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    private Byte createType;
    @NotNull
    private Integer dataSourceComposeType;
    private Integer calType;
    @NotNull
    private Integer displayOrder;
    private String exValues;
    private String script;
    @NotEmpty
    @Valid
    @Size(max = 10)
    private List<@NotNull DataSourceTokenItem> tokenList;
    @Valid
    @Size(max = 100)
    private List<@NotNull FormulaItem> formulaList;

    @Override
    public ResultWrapper validate() {

        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        QueryWrapper<TbMonitorType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("monitorType",monitorType);
        queryWrapper.and(
                wrapper -> wrapper.eq("companyID", companyID).or().eq("companyID", -1 )
        );
        if (tbMonitorTypeMapper.selectOne(queryWrapper) == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"监测类型不存在");
        }
        if (!CreateType.isValid(createType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"创建类型不合法");
        }
        if (calType == null){
            calType = -1;
        }
        if (!CalType.isValid(calType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "计算方式不合法");
        }
        if (!DataSourceComposeType.isValid(dataSourceComposeType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板数据来源类型不合法");
        }
        if (!StringUtils.isBlank(exValues)&& !JSONUtil.isTypeJSON(exValues)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"额外属性不合法");
        }
        if (ObjectUtil.isAllNotEmpty(script, formulaList)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"脚本与公式需要二选一");
        }
        if (ObjectUtil.isNotEmpty(tokenList)){
            if (tokenList.stream().anyMatch(item -> !DatasourceType.isValid(item.getDatasourceType()))){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "数据源的类型非法");
            }
        }
        if (ObjectUtil.isNotEmpty(formulaList)){
            if (formulaList.stream().map(FormulaItem::getFieldID).distinct().count() != formulaList.size()){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "不可重复设置公式");
            }
            TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
            if (tbMonitorTypeFieldMapper.selectCount(new QueryWrapper<TbMonitorTypeField>()
                    .eq("monitorType", monitorType)
                    .in("ID",formulaList.stream().map(FormulaItem::getFieldID).collect(Collectors.toList())))
                    !=formulaList.size()){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有属性不属于该监测类型或不存在");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
