package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-29 16:51
 **/
@Data
public class AddTemplateParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

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
        if (companyID == null){
            companyID = -1;
        }
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        QueryWrapper<TbMonitorType> monitorTypeQueryWrapper = new QueryWrapper<>();
        monitorTypeQueryWrapper.eq("companyID", companyID).eq("monitorType",monitorType);
        if (tbMonitorTypeMapper.selectOne(monitorTypeQueryWrapper) == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"监测类型不存在");
        }
        if (!CreateType.isValid(createType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"创建类型不合法");
        }
        // TODO 校验dataSourceComposeType, calType
        if (calType == null){
            calType = -1;
        }
        if (!StringUtils.isBlank(exValues)&& !JSONUtil.isTypeJSON(exValues)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"额外属性不合法");
        }
        if (ObjectUtil.isAllNotEmpty(script, formulaList)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"脚本与公式需要二选一");
        }
        // TODO 校验tokenListdatasourceType
        // TODO 校验formulaList中fieldID
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
