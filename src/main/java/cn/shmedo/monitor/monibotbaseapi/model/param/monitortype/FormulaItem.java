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
 * @create: 2023-03-29 17:00
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormulaItem {
    @NotNull
    private Integer fieldID;
    @NotBlank
    @Size(max = 2000)
    private String formula;
    @NotBlank
    @Size(max = 5000)
    private String displayFormula;

    // TODO 暂时设置为非必填
    private Integer fieldCalOrder;
}
