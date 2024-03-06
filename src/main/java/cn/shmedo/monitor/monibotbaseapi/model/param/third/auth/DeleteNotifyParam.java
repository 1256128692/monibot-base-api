package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-06 13:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteNotifyParam {
    @NotNull
    private Integer companyID;
    @NotEmpty
    private List<Integer> notifyIDList;
}
