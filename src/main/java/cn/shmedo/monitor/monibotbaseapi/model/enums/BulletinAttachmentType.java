package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-28 10:08
 */
@Getter
@RequiredArgsConstructor
public enum BulletinAttachmentType {
    OSS_FILE(1, "公告文件附件"), LOCAL_FILE(2, "公告内嵌文件");
    private final Integer code;
    private final String desc;
}
