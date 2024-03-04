package cn.shmedo.monitor.monibotbaseapi.model.param.notify;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryNotifyPageListParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-24 13:59
 */
@Data
public class QueryNotifyPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "页大小不能为空")
    @Range(min = 1, max = 100, message = "分页大小必须在1~100之间")
    private Integer pageSize;
    @Min(value = 1, message = "当前页不能小于1")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;

    /** 1.报警 2.事件 3.工单 */
    private Integer type;
    private Integer serviceID;
    @JsonProperty("queryKey")
    private String queryCode;
    @Range(min = 0, max = 1, message = "已读/未读状态 0.未读 1.已读")
    private Integer status;
    @JsonProperty("begin")
    private Date startTime;
    @JsonProperty("end")
    private Date endTime;

    /** 时间排序 0.降序（默认） 1.升序 */
    private Integer timeOrder;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    public QueryNotifyPageListParam build() {
        QueryNotifyPageListParam param = new QueryNotifyPageListParam();
        BeanUtil.copyProperties(this, param);
        param.setQueryKey(queryCode);
        return param;
    }
}
