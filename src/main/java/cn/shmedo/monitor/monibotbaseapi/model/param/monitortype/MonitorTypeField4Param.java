package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-28 16:24
 **/
@Data
public class MonitorTypeField4Param {
    @NotBlank
    @Size(max = 50)
    private String fieldName;
    @NotBlank
    @Size(max = 50)
    private String fieldToken;
    @NotNull
    private Integer fieldClass;
    @NotBlank
    private String fieldDataType;
    @NotNull
    private Integer fieldUnitID;
    @NotNull
    private Integer createType;
    @Size(max = 500)
    private String fieldDesc;
    @Size(max = 500)
    private String exValues;


}
