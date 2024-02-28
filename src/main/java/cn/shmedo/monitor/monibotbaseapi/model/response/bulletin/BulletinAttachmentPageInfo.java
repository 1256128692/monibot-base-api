package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinAttachment;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:45
 */
@Data
public class BulletinAttachmentPageInfo {
    private Integer id;
    private String fileName;
    private String fileType;
    private Integer fileSize;
    private String createUser;
    private Date createTime;
    private String filePath;
    @JsonIgnore
    private static final Function<Double, String> FORMATTER = num -> String.valueOf(BigDecimal.valueOf(num)
            .setScale(2, RoundingMode.HALF_UP).doubleValue());

    public static BulletinAttachmentPageInfo build(final TbBulletinAttachment attachment, final FileInfoResponse response) {
        BulletinAttachmentPageInfo info = new BulletinAttachmentPageInfo();
        BeanUtil.copyProperties(attachment, info);
        BeanUtil.copyProperties(response, info);
        info.setFilePath(response.getAbsolutePath());
        return info;
    }

    @JsonProperty("fileSizeDesc")
    private String fileSizeDesc() {
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
