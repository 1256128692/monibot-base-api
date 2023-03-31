package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 14:30
 **/
@Data
public class ParamItem {
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
    @NotBlank
    private Integer paUnitID;
    @Size(max = 200)
    private String paDesc;
}
