package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author wuxl
 * @Date 2024/1/23 13:27
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.dashboard
 * @ClassName: ResourceOverview
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResourceOverviewRes {
    private long companyCount;
    private long areaCount;
    private long projectCount;
}
