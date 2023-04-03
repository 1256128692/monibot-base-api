package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 14:30
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamItem {
    @JsonProperty("ID")
    private Integer ID;
    @NonNull
    private Integer subjectID;
    @NotBlank
    private String dataType;
    @NotBlank
    @Size(max = 50)
    private String token;
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotBlank
    @Size(max = 1000)
    private String paValue;
    @NotNull
    private Integer paUnitID;
    @Size(max = 200)
    private String paDesc;
}
