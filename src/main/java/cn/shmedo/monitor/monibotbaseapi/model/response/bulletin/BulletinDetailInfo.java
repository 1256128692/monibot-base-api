package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-01 15:13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BulletinDetailInfo extends BulletinDataBaseInfo {
    private Integer status;
    private Date createTime;
    @JsonIgnore
    private String attachmentStr;

    @JsonProperty("attachmentDataList")
    private List<BulletinDetailAttachmentInfo> attachmentDataList() {
        return Optional.ofNullable(attachmentStr).filter(StrUtil::isNotEmpty).map(u -> u.split(",")).map(Arrays::stream)
                .map(u -> u.distinct().toList()).flatMap(u -> Optional.of(ContextHolder.getBean(FileService.class))
                        .map(w -> w.getFileUrlList(u, getCompanyID())).map(w -> w.stream().map(s ->
                                BulletinDetailAttachmentInfo.builder().fileName(s.getFileName()).fileType(s.getFileType())
                                        .filePath(s.getAbsolutePath()).build()).toList())).orElse(List.of());
    }
}
