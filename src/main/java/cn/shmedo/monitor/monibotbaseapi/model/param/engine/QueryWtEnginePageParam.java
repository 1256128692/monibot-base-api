package cn.shmedo.monitor.monibotbaseapi.model.param.engine;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QueryWtEnginePageParam {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    private Integer projectID;
    private String engineName;
    private Boolean enable;
    private Integer monitorItemID;
    private Integer monitorPointID;
    @Min(value = 1, message = "当前页不能小于1")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;
    @Size(min = 1, max = 100, message = "分页大小必须在1~100")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;
}
