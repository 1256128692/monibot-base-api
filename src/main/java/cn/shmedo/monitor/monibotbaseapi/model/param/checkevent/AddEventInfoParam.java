package cn.shmedo.monitor.monibotbaseapi.model.param.checkevent;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AddEventInfoParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer serviceID;
    private Integer taskID;
    private Integer orderID;
    @NotNull
    private Integer typeID;
    @NotBlank
    private String address;
    @NotBlank
    private String location;
    @NotBlank
    private String describe;
    private List<String> ossKeyList;
    private LocalDateTime handleTime;
    @NotNull
    @Min(0)
    @Max(1)
    private Integer status;
    private String comment;
    private String exValue;
    @JsonIgnore
    private String annexes;
    @JsonIgnore
    private LocalDateTime reportTime;
    @JsonIgnore
    private Integer reportUserID;
    @JsonIgnore
    private String serialNumber;

    @JsonIgnore
    private Integer subjectID;
    @Override
    public ResultWrapper validate() {
        TbCheckEventMapper checkEventMapper = ContextHolder.getBean(TbCheckEventMapper.class);
        List<TbCheckEvent> checkEvents = checkEventMapper.selectList(null);

        subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayString = today.format(formatter);

        // 筛选出今天的任务编号
        List<String> todaySerialNumbers = checkEvents.stream()
                .map(TbCheckEvent::getSerialNumber)
                .filter(serialNumber -> serialNumber.startsWith("E" + todayString))
                .collect(Collectors.toList());

        // 如果今天没有任务编号，则直接生成以今天日期开头的编号
        if (todaySerialNumbers.isEmpty()) {
            serialNumber = "E" + todayString + "00001";
        } else {
            // 提取最后5位数字并找到最大值
            int maxNumber = todaySerialNumbers.stream()
                    .mapToInt(serialNumber -> Integer.parseInt(serialNumber.substring(11)))
                    .max()
                    .orElse(0);

            // 构建新的编号
            serialNumber = "E" + todayString + String.format("%05d", maxNumber + 1);
        }

        reportUserID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        reportTime = DateUtil.date().toLocalDateTime();

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
        vo.setProjectID(this.projectID);
        vo.setOrderID(this.orderID);
        vo.setTaskID(this.taskID);
        vo.setServiceID(this.serviceID);
        vo.setTypeID(this.typeID);
        vo.setSerialNumber(this.serialNumber);
        vo.setAddress(this.address);
        vo.setLocation(this.location);
        vo.setDescribe(this.describe);
        vo.setAnnexes(this.annexes);
        vo.setReportUserID(this.reportUserID);
        vo.setReportTime(this.reportTime);
        vo.setHandleTime(this.handleTime);
        vo.setStatus(this.status);
        vo.setComment(this.comment);
        vo.setExValue(this.exValue);

        vo.setCreateUserID(this.subjectID);
        vo.setCreateTime(DateUtil.date().toLocalDateTime());
        vo.setUpdateUserID(this.subjectID);
        vo.setUpdateTime(DateUtil.date().toLocalDateTime());

        return vo;
    }
}
