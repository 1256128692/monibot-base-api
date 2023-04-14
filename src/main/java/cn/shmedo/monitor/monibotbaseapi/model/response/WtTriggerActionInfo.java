package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WtTriggerActionInfo extends TbWarnAction {
    private Integer engineID;
    private String warnName;
    private Integer warnLevel;
    private Integer warnID;     //冗余,保证一定有报警状态ID
    private Integer fieldID;
    private String compareRule;
    private String triggerRule;
    private final List<TbWarnAction> action = new ArrayList<>();

    public WtTriggerActionInfo setAction(TbWarnAction data) {
        TbWarnAction res = new TbWarnAction();
        BeanUtils.copyProperties(data, res);
        this.action.add(res);
        return this;
    }

    public static WtWarnStatusInfo build(WtTriggerActionInfo info) {
        WtWarnStatusInfo res = new WtWarnStatusInfo();
        BeanUtils.copyProperties(info, res);
        return res;
    }

    public static WtWarnStatusDetailInfo buildDetail(WtTriggerActionInfo info) {
        WtWarnStatusDetailInfo res = new WtWarnStatusDetailInfo();
        BeanUtils.copyProperties(info, res);
        return res.setMetadataID(info.getFieldID());
    }
}
