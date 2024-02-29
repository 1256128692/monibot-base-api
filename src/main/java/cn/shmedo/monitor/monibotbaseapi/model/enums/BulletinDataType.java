package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-28 17:54
 */
@Getter
@RequiredArgsConstructor
public enum BulletinDataType {
    OTHER(0, "其他"), INDUSTRY_POLICY(1, "行业政策"), IMPORTANT_NEWS(2, "重要新闻"),
    WORK_BULLETIN(3, "工作公告");
    private final Integer code;
    private final String desc;

    public static boolean isValid(final Integer code) {
        return Optional.ofNullable(code).map(u -> Arrays.stream(values()).anyMatch(w -> w.getCode().equals(u))).orElse(false);
    }
}
