package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinAttachment;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.util.FileSizeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

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

    public static BulletinAttachmentPageInfo build(final TbBulletinAttachment attachment, final FileInfoResponse response) {
        BulletinAttachmentPageInfo info = new BulletinAttachmentPageInfo();
        BeanUtil.copyProperties(attachment, info);
        BeanUtil.copyProperties(response, info);
        info.setFilePath(response.getAbsolutePath());
        return info;
    }

    @JsonProperty("fileSizeDesc")
    private String fileSizeDesc() {
        return FileSizeUtil.getFileSizeDesc(fileSize);
    }
}
