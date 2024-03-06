package cn.shmedo.monitor.monibotbaseapi.model.param.checkevent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEventType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEventTypeParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @NotNull(message = "事件ID不能为空")
    private Integer id;
    private String name;

    @NotNull(message = "服务ID不能为空")
    private Integer serviceID;
    private String exValue;

    @JsonIgnore
    private Integer updateUserID;
    @JsonIgnore
    private LocalDateTime updateTime;

    @Override
    public ResultWrapper validate() {
        TbCheckEventTypeMapper checkEventTypeMapper = ContextHolder.getBean(TbCheckEventTypeMapper.class);
        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        TbCheckEventType checkEventType = checkEventTypeMapper.selectOne(new QueryWrapper<TbCheckEventType>()
                .eq("name", name)
                .eq("serviceID", serviceID)
                .ne("ID", id));

        if (ObjectUtil.isNotNull(checkEventType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "事件类型名称不允许重复");
        }
        updateUserID = subjectID;
        updateTime = DateUtil.date().toLocalDateTime();

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

    public TbCheckEventType toRawVo() {
        TbCheckEventType vo = new TbCheckEventType();
        vo.setName(this.name);
        vo.setExValue(this.exValue);
        vo.setServiceID(this.serviceID);
        vo.setUpdateTime(this.updateTime);
        vo.setUpdateUserID(this.updateUserID);
        return vo;
    }
}
