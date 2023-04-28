package cn.shmedo.monitor.monibotbaseapi.model.param.report;

import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.ErrorConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryMaxPeriodRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryMaxPeriodResponse;
import cn.shmedo.monitor.monibotbaseapi.service.third.wt.WtReportService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 10:30
 */
@Data
public class WtQueryReportParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "简报类型不能为空")
    @Range(max = 4, message = "简报类型 0自定义 1周报 2月报 4年报")
    private Integer reportType;
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    private List<Integer> projectIDList;
    @JsonIgnore
    private String period;

    @Override
    public ResultWrapper validate() {
        Date current = new Date();
        switch (reportType) {
            case 0 -> {
                if (DateUtil.betweenYear(startTime, endTime, false) >= 1) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "时间范围最大限制一年");
                }
                FileConfig config = ContextHolder.getBean(FileConfig.class);
                WtReportService wtReportService = ContextHolder.getBean(WtReportService.class);
                QueryMaxPeriodRequest request = new QueryMaxPeriodRequest();
                request.setYear(DateUtil.year(endTime));
                request.setCompanyID(companyID);
                Optional.ofNullable(
                                wtReportService.queryMaxPeriod(request, config.getAuthAppKey(), config.getAuthAppSecret()))
                        .map(ResultWrapper::getData).map(QueryMaxPeriodResponse::getPeriod)
                        .ifPresent(u -> period = DateUtil.year(endTime) + "年" + (u + 1) + "期");
            }
            case 1 -> {
                startTime = DateUtil.beginOfWeek(startTime);
                endTime = DateUtil.endOfWeek(endTime);
                if (startTime.after(DateUtil.beginOfWeek(current))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前周和未来周无法选择");
                }
                period = DateUtil.year(startTime) + "第" + DateUtil.weekOfYear(endTime) + "周";
            }
            case 2 -> {
                startTime = DateUtil.beginOfMonth(startTime);
                endTime = DateUtil.endOfMonth(endTime);
                if (startTime.after(DateUtil.beginOfMonth(current))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当月和未来月无法选择");
                }
                period = DateUtil.year(startTime) + "年" + DateUtil.month(endTime) + "月";
            }
            case 3 -> {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "暂不支持季度报");
            }
            case 4 -> {
                //年报
                startTime = DateUtil.beginOfYear(startTime);
                endTime = DateUtil.endOfYear(endTime);
                if (startTime.after(DateUtil.beginOfYear(current))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当年和未来年无法选择");
                }
                period = DateUtil.year(startTime) + "年";
            }
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
