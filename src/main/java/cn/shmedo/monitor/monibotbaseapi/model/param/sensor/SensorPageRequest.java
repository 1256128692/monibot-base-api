package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

/**
 * 传感器分页请求体
 *
 * @author Chengfs on 2023/3/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SensorPageRequest extends SensorListRequest {

    /**
     * 分页大小 (1-100)
     */
    @Range(min = 1, max = 100, message = "分页大小必须在1-100之间")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    /**
     * 当前页码 (大于0)
     */
    @Range(min = 1, message = "当前页码必须大于0")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;
}

    
    