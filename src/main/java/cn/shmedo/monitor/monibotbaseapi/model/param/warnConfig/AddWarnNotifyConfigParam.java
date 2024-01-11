package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 11:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddWarnNotifyConfigParam extends CompanyPlatformParam {
    private Boolean allProject;
    @Size(min = 1, message = "选中的工程ID数不能小于1")
    private List<Integer> projectIDList;
    @NotNull(message = "通知配置类型不能为空")
    @Range(min = 1, max = 2, message = "通知配置类型 1.设备报警通知 2.数据报警通知")
    private Integer notifyType;
    @NotEmpty(message = "通知方式不能为空")
    private List<@Valid @Range(min = 1, max = 2, message = "通知方式(多选),枚举值: 1.平台消息 2.短信") Integer> notifyMethod;
    private List<Integer> warnLevel;
    private List<Integer> deptList;
    private List<Integer> userList;
    private List<Integer> roleList;
    private String exValue;
}
