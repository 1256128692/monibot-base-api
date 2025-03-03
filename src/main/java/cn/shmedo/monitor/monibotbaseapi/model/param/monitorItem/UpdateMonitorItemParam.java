package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-10 17:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMonitorItemParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer monitorItemID;
    @NotBlank
    @Size(max = 20)
    private String alias;
    @NotNull
    private Boolean enable;
    @Size(max = 500)
    private String exValue;
    private Integer displayOrder;
    @Valid
    private List<@NotNull FieldItem> fieldItemList;
    @JsonIgnore
    private TbMonitorItem  tbMonitorItem;

    public TbMonitorItem update(Integer userID, Date now){
        tbMonitorItem.setAlias(alias);
        tbMonitorItem.setName(alias);
        tbMonitorItem.setEnable(enable);
        if (enable == false) {
            tbMonitorItem.setMonitorClass(null);
        }
        tbMonitorItem.setExValue(exValue);
        tbMonitorItem.setDisplayOrder(displayOrder);
        tbMonitorItem.setUpdateUserID(userID);
        tbMonitorItem.setUpdateTime(now);
        return tbMonitorItem;
    }

    @Override
    public ResultWrapper validate() {
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        tbMonitorItem = tbMonitorItemMapper.selectByPrimaryKey(monitorItemID);
        if (tbMonitorItem == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
        }
        if (!projectID.equals(tbMonitorItem.getProjectID())){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不属于该工程项目");
        }
        if (ObjectUtil.isNotEmpty(fieldItemList)){
            TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
            List<Integer> typeFieldIDList = fieldItemList.stream().map(FieldItem::getMonitorTypeFieldID).distinct().toList();
            if (typeFieldIDList.size()!=fieldItemList.size()){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性有重复");
            }
            if (tbMonitorTypeFieldMapper.selectCount(
                    new QueryWrapper<TbMonitorTypeField>().eq("monitorType", tbMonitorItem.getMonitorType()).in("id", typeFieldIDList)
            ) != typeFieldIDList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性有不属于该监测类型的");
            }
        }
        if (StringUtils.isNotBlank(exValue) && JSONUtil.isTypeJSON(exValue)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目额外属性不合法");
        }
        if (tbMonitorItemMapper.selectCount(
                new QueryWrapper<TbMonitorItem>().eq("projectID", projectID).eq("alias", alias).ne("id", monitorItemID)
        )>0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该项目下监测项目别名已存在");
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
