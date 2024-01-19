package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import lombok.Data;
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
        private int province;
        private String provinceName;
        private int city;
        private String cityName;
        private int area;
        private String areaName;
        private int town;
        private String townName;
    }
}
