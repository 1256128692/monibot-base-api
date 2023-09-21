package cn.shmedo.monitor.monibotbaseapi.model.response.documentfile;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDocumentFile;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author wuxl
 * @Date 2023/9/20 9:34
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.documentfile
 * @ClassName: DocumentFileResponse
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentFileResponse {
    /**
     * 主键
     */
    @JsonProperty("ID")
    private Integer id;

    /**
     * 对象类型  1.工程项目  2.其他设备
     */
    private Integer subjectType;

    /**
     * 对象ID
     */
    @JsonProperty("subjectID")
    private Integer subjectId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件地址
     */
    private String filePath;

    /**
     * 文件描述
     */
    private String fileDesc;

    /**
     * 拓展字段
     */
    private String exValue;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createTime;

    /**
     * 创建用户ID
     */
    @JsonProperty("createUserID")
    private Integer createUserId;

    /**
     * 创建用户名称
     */
    private String createUserName;

    public static DocumentFileResponse getDocumentFile(TbDocumentFile tbDocumentFile) {
        return DocumentFileResponse.builder()
                .id(tbDocumentFile.getID())
                .subjectId(tbDocumentFile.getSubjectID())
                .subjectType(tbDocumentFile.getSubjectType())
                .fileType(tbDocumentFile.getFileType())
                .fileName(tbDocumentFile.getFileName())
                .fileSize(tbDocumentFile.getFileSize())
                .filePath(tbDocumentFile.getFilePath())
                .fileDesc(tbDocumentFile.getFileDesc())
                .exValue(tbDocumentFile.getExValue())
                .createTime(TimeUtil.getDate(tbDocumentFile.getCreateTime()))
                .createUserId(tbDocumentFile.getCreateUserID()).build();
    }
}
