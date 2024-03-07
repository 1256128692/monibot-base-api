package cn.shmedo.monitor.monibotbaseapi.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-07 13:17
 */
public class FileSizeUtil {
    private static final Function<Double, String> FORMATTER = num -> String.valueOf(BigDecimal.valueOf(num)
            .setScale(2, RoundingMode.HALF_UP).doubleValue());

    public static String getFileSizeDesc(final Integer fileSize) {
        return Optional.ofNullable(fileSize).map(u -> {
            if (u < 1024L) {
                return u + "B";
            }
            if (u < 1024L * 1024) {
                return FORMATTER.apply((double) u / 1024L) + "KB";
            }
            if (u < 1024L * 1024 * 1024) {
                return FORMATTER.apply((double) u / (1024L * 1024)) + "MB";
            }
            return FORMATTER.apply((double) u / (1024L * 1024 * 1024)) + "GB";
        }).orElse(null);
    }
}
