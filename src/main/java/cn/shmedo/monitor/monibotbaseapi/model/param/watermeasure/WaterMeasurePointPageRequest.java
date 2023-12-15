package cn.shmedo.monitor.monibotbaseapi.model.param.watermeasure;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.Collection;
import java.util.List;

/**
 * @author Chengfs on 2023/12/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaterMeasurePointPageRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    private String keyword;

    @NotNull
    @Positive
    private Integer currentPage;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer pageSize;

    @JsonIgnore
    private Collection<Integer> projectIdList;

    @Override
    public ResultWrapper<?> validate() {
        this.projectIdList = PermissionUtil.getHavePermissionProjectList(companyID, projectIdList);

        return null;
    }

    @Override
    public List<Resource> parameter() {
        return this.projectIdList.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}