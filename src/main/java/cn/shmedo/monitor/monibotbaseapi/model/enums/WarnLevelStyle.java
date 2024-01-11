package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:51
 */
@Getter
@RequiredArgsConstructor
public enum WarnLevelStyle {
    COLOR(1, "红,橙,黄,蓝"), ARAB(2, "1级,2级,3级,4级"), ROMA(3, "Ⅰ级,Ⅱ级,Ⅲ级,Ⅳ级");
    private final Integer code;
    private final String desc;
}
