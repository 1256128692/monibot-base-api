package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-29 16:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryDepartmentIncludeUserInfoListParameter {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于0")
    private Integer companyID;
    private String departmentName;
    @NotNull(message = "获取深度不能为空")
    @Min(value = 1, message = "获取深度至少为1")
    @Max(value = 3, message = "获取深度最多为3")
    private Integer fetchDepth;
    private Integer dataSourceType;
}
