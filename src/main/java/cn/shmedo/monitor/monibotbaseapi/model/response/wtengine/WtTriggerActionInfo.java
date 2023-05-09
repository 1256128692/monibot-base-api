package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IFieldName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WtTriggerActionInfo extends TbWarnAction implements IFieldName {
    private Integer engineID;
    private String warnName;
    private Integer warnLevel;
    private Integer warnID;     //冗余,保证一定有报警状态ID
    @JsonIgnore
    private Integer monitorType;
    @JsonIgnore
    private String fieldName;
    private String fieldToken;
    private String compareRule;
    private String triggerRule;
    private final List<TbWarnAction> action = new ArrayList<>();

    public WtTriggerActionInfo setAction(TbWarnAction data) {
        Optional.ofNullable(data).filter(u -> Objects.nonNull(data.getID())).ifPresent(
                u -> {
                    TbWarnAction res = new TbWarnAction();
                    BeanUtil.copyProperties(u, res);
                    this.action.add(res);
                });
        return this;
    }

    public static WtWarnStatusInfo build(WtTriggerActionInfo info) {
        WtWarnStatusInfo res = new WtWarnStatusInfo();
        BeanUtil.copyProperties(info, res);
        return res;
    }

    public static WtWarnStatusDetailInfo buildDetail(WtTriggerActionInfo info) {
        WtWarnStatusDetailInfo res = new WtWarnStatusDetailInfo();
        BeanUtil.copyProperties(info, res);
        return res;
    }
}
