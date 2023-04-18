package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WtWarnStatusInfo {
    private Integer warnID;
    private String warnName;
    private Integer warnLevel;
    private String compareRule;
    private List<TbWarnAction> action;
}
