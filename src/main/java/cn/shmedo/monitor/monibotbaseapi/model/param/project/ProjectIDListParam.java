package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @Author cyf
 * @Date 2023/2/28 14:12
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.project
 * @ClassName: ProjectIDListParam
 * @Description:
 * @Version 1.0
 */
@Data
public class ProjectIDListParam {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Size(min = 1, max = 100)
    private List<Integer> dataIDList;
}
