package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-25 15:40
 **/
@Data
public class FieldItem {
    @NotNull
    private Integer monitorTypeFieldID;
    @NotBlank
    private String alias;
}
