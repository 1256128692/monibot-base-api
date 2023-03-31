package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-29 17:00
 **/
@Data
public class FormulaItme {
    @NonNull
    private Integer fieldID;
    @NotBlank
    @Size(max = 2000)
    private String formula;
    @NotBlank
    @Size(max = 2000)
    private String displayFormula;
    @NotNull
    private Integer fieldCalOrder;
}
