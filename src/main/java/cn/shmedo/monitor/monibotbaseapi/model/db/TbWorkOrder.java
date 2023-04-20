package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-19 09:49
 */
@Data
@TableName("tb_work_order")
public class TbWorkOrder {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;
    @TableField("CompanyID")
    private Integer companyID;
    @TableField("OrderCode")
    private String orderCode;
    @TableField("`Status`")
    private Integer status;
    @TableField("`Type`")
    private Integer type;
    @TableField("Organization")
    private String organization;
    @TableField("Solution")
    private String solution;
    @TableField("DispatcherName")
    private String dispatcherName;
    @TableField("DispatchTime")
    private Date dispatchTime;
    @TableField("DisposerName")
    private String disposerName;
    @TableField("DisposeTime")
    private Date disposeTime;
}
