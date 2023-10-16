package cn.shmedo.monitor.monibotbaseapi.model.response.project;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author wuxl
 * @Date 2023/10/16 11:52
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.project
 * @ClassName: QueryPropertyValuesResponse
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class QueryPropertyValuesResponse {

    private Integer modelID;

    private String modelName;

    private List<PropertyIdAndValue> propertyIdAndValueList;
}
