package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 19:09
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFieldItem {
    @NotNull
    @JsonProperty("ID")
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
