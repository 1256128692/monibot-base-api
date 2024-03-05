package cn.shmedo.monitor.monibotbaseapi.model.param.checkevent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class UpdateEventInfoParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;
    @NotNull
    private Integer eventID;
    private Integer projectID;
    private Integer orderID;
    private Integer taskID;
    private Integer serviceID;
    private Integer typeID;
    private String address;
    private String location;
    private String describe;
    private List<String> ossKeyList;
    private LocalDateTime handleTime;
    private Integer status;
    private String comment;
    private String exValue;
    @JsonIgnore
    private String annexes;
    @JsonIgnore
    private Integer subjectID;
    @Override
    public ResultWrapper validate() {
//        TbCheckEventMapper checkEventMapper = ContextHolder.getBean(TbCheckEventMapper.class);
        subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();

        if (!CollectionUtil.isNullOrEmpty(ossKeyList)) {
            annexes = JSONUtil.toJsonStr(ossKeyList);
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

    public TbCheckEvent toTbVo() {
        TbCheckEvent vo = new TbCheckEvent();
        vo.setID(this.eventID);
        vo.setProjectID(this.projectID);
        vo.setOrderID(this.orderID);
        vo.setTaskID(this.taskID);
        vo.setServiceID(this.serviceID);
        vo.setTypeID(this.typeID);
        vo.setAddress(this.address);
        vo.setLocation(this.location);
        vo.setDescribe(this.describe);
        vo.setAnnexes(this.annexes);
        vo.setHandleTime(this.handleTime);
        vo.setStatus(this.status);
        vo.setComment(this.comment);
        vo.setExValue(this.exValue);

        vo.setUpdateUserID(this.subjectID);
        vo.setUpdateTime(DateUtil.date().toLocalDateTime());

        return vo;
    }

}
