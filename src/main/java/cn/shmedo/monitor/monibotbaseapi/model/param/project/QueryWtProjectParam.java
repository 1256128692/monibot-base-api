package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.*;

/**
 * 查询水利工程项目简要列表参数
 *
 * @author Chengfs on 2023/4/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryWtProjectParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;

    @Range(min = 1, max = 3)
    private Integer projectType;

    private String projectName;

    private String v1;

    private String v2;

    @JsonIgnore
    private Collection<Integer> projectIDSet = new HashSet<>();

    @JsonIgnore
    private List<QueryProjectListRequest.Property> propertyList = new ArrayList<>();

    public static final List<String> v1KeySet = List.of("水库规模", "河道起点", "堤防级别");

    public static final List<String> v2KeySet = List.of("所在河流", "河道重点", "堤防类型");

    @Override
    public ResultWrapper<?> validate() {
        if (projectType != null) {
//            Assert.isTrue(projectType > 0 && projectType <= v1KeySet.size());
            Optional.ofNullable(v1).filter(StrUtil::isNotBlank).ifPresent(v ->
                    propertyList.add(new QueryProjectListRequest.Property(v1KeySet.get(projectType - 1), v)));
            Optional.ofNullable(v2).filter(StrUtil::isNotBlank).ifPresent(v ->
                    propertyList.add(new QueryProjectListRequest.Property(v2KeySet.get(projectType - 1), v)));
        }else {
            Optional.ofNullable(v1).filter(StrUtil::isNotBlank).ifPresent(v ->
                    propertyList.addAll(v1KeySet.stream().map(key ->
                            new QueryProjectListRequest.Property(key, v1)).toList()));
            Optional.ofNullable(v2).filter(StrUtil::isNotBlank).ifPresent(v ->
                    propertyList.addAll(v2KeySet.stream().map(key ->
                            new QueryProjectListRequest.Property(key, v2)).toList()));
        }

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}