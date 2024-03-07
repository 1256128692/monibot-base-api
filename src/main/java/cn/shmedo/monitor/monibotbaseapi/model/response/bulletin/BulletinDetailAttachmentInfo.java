package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

import cn.shmedo.monitor.monibotbaseapi.util.FileSizeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-01 15:15
 */
@Data
@Builder
public class BulletinDetailAttachmentInfo {
    private String fileName;
    private String fileType;
    private String filePath;
    private Integer fileSize;

    @JsonProperty("fileSizeDesc")
    private String fileSizeDesc() {
        return FileSizeUtil.getFileSizeDesc(fileSize);
    }
}
