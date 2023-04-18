package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnRuleMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class AddWtEngineParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "请选择所属工程项目")
    @Min(value = 1, message = "工程项目ID不能小于1")
    private Integer projectID;
    @Size(min = 1, max = 30, message = "规则名称仅允许1~30个字符")
    @NotEmpty(message = "请填写规则名称")
    private String engineName;
    @Size(min = 1, max = 200, message = "规则简介仅运行1~200个字符")
    @NotEmpty(message = "请填写规则简介")
    private String engineDesc;
    @NotNull(message = "请选择所属监测项目")
    private Integer monitorItemID;
    @NotNull(message = "请选择监测点")
    private Integer monitorPointID;

    public static TbWarnRule build(AddWtEngineParam param) {
        Date current = new Date();
        TbWarnRule res = new TbWarnRule();
        res.setName(param.getEngineName());
        res.setDesc(param.getEngineDesc());
        res.setCreateTime(current);
        res.setUpdateTime(current);
        res.setMonitorItemID(param.getMonitorItemID());
        res.setMonitorPointID(param.getMonitorPointID());
        res.setProjectID(param.projectID);
        res.setEnable(false);
        return res;
    }

    @Override
    public ResultWrapper validate() {
        TbWarnRuleMapper tbWarnRuleMapper = ContextHolder.getBean(TbWarnRuleMapper.class);
        if (tbWarnRuleMapper.selectOne(new LambdaQueryWrapper<TbWarnRule>().eq(TbWarnRule::getName, engineName)) != null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "规则名称重复");
        }
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectOne(new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getID, projectID).select(TbProjectInfo::getID)) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        if (tbMonitorItemMapper.selectOne(new LambdaQueryWrapper<TbMonitorItem>()
                .eq(TbMonitorItem::getID, monitorItemID).select(TbMonitorItem::getID)) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
        }
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        if (tbMonitorPointMapper.selectOne(new LambdaQueryWrapper<TbMonitorPoint>()
                .eq(TbMonitorPoint::getID, monitorPointID).select(TbMonitorPoint::getID)) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
        }
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
