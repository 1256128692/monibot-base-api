package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kotlin.internal.InlineOnly;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 19:09
 **/
@Data
public class UpdateFieldTime {
    @NotNull
    private Integer ID;
    @NotBlank
    @Size(max = 50)
    private String  fieldName;
    @NotBlank
    @Size(max = 50)
    private String fieldDataType;
    @NotNull
    private Integer fieldUnitID;
    @Size(max = 500)
    private String desc;
    @Size(max = 500)
    private String exValues;
}
