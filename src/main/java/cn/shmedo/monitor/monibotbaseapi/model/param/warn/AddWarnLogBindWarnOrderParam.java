package cn.shmedo.monitor.monibotbaseapi.model.param.warn;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class AddWarnLogBindWarnOrderParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    private Integer projectID;
    @NotNull(message = "报警记录ID不能为空")
    private Integer warnID;

    @NotNull(message = "报警类型不能为空")
    private Integer warnType;

    @NotNull(message = "工单名称不能为空")
    private String warnOrderName;

    @NotNull(message = "工单类型来源不能为空")
    private Integer sourceType;

    @NotNull(message = "工单配置信息不能为空")
    private String exValue;

    @JsonIgnore
    private String orderCode;

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Integer status;

    @JsonIgnore
    private Integer ID;

    @Override
    public ResultWrapper<?> validate() {
        orderCode = UUID.randomUUID(true).toString();
        createTime = DateUtil.date();
        // 订单默认为未处理状态
        status = DefaultConstant.ORDER_NOT_STAT;
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
