package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @author wuxl
 * @TableName tb_document_file
 */
@Data
@TableName("tb_document_file")
public class TbDocumentFile implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "ID",type = IdType.AUTO)
    private Integer ID;

    /**
     * 对象类型  1.工程项目  2.其他设备
     */
    private Integer subjectType;

    /**
     * 对象ID
     */
    private Integer subjectID;

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
    private LocalDateTime createTime;

    /**
     * 创建用户ID
     */
    private Integer createUserID;

    private static final long serialVersionUID = 1L;
}