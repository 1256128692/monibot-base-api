package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Author wuxl
 * @Date 2024/1/18 11:34
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.project
 * @ClassName: ProjectInfoCache
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ProjectInfoCache extends TbProjectInfo {
    /**
     * 项目类型名称
     */
    private String projectTypeName;

    /**
     * 项目主类型名称
     */
    private String projectMainTypeName;

    /**
     * 行政区划
     */
    private LocationInfo locationInfo;

    @Data
    @Accessors(chain = true)
    public static class LocationInfo{
        private Integer province;
        private String provinceName;
        private Integer city;
        private String cityName;
        private Integer area;
        private String areaName;
        private Integer town;
        private String townName;
    }
}
