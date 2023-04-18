package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 16:14
 **/
@Data
public class AddPointItem {

    @NotBlank
    @Size(max = 50)
    private String name;
    @NotNull
    private Boolean enable;
    @Size(max = 100)
    private String gpsLocation;
    @Size(max = 100)
    private String imageLocation;
    @Size(max = 100)
    private String overallViewLocation;
    @Size(max = 100)
    private String spatialLocation;
    @Size(max = 500)
    private String exValues;
    private Integer displayOrder;
}
