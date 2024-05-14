package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_eigen_value_relation")
public class TbEigenValueRelation implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
    * 特征ID
    */
    @TableField("EigenValueID")
    private Integer eigenValueID;

    /**
    * 监测点ID
    */
    @TableField("MonitorPointID")
    private Integer monitorPointID;

    @Serial
    private static final long serialVersionUID = 1L;
}