package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-29 16:58
 **/
@Data
public class DataSourceTokenItem {
    @NotNull
    private Integer datasourceType;
    @NotBlank
    private String token;
}
