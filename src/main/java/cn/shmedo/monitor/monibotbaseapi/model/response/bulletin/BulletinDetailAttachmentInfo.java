package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

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
}
