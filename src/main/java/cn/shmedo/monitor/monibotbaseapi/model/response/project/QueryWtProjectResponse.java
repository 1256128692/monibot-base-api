package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 查询水利工程项目简要列表返回参数
 *
 * @author Chengfs on 2023/4/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryWtProjectResponse {

    private List<WaterInfo> waterInfo;

    public record WaterInfo(Integer type, String typeName, Integer count, List<Detail> dataList) {
    }

    public record Detail(Integer projectID, String projectName,
                         String projectShortName, String location, String v1, String v2) {
    }
}