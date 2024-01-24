package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import cn.shmedo.monitor.monibotbaseapi.model.response.project.ProjectInfoCache;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author wuxl
 * @Date 2024/1/23 17:10
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.dashboard
 * @ClassName: ProvinceProjectRes
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class ProvinceProjectDetailRes {
    private Integer provinceCode;
    private String provinceName;
    private int projectCount;
    private List<Project> projectList;

    @Data
    @Accessors(chain = true)
    public static class Project{
        @JsonProperty("projectID")
        private Integer ID;
        private String projectName;
        private Integer projectType;
        private String projectTypeName;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private String projectAddress;
        private ProjectInfoCache.LocationInfo locationInfo;
    }
}
