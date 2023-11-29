package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbEigenValueRelation implements Serializable {
    private Integer id;

    /**
    * 特征ID
    */
    private Integer eigenValueID;

    /**
    * 监测点ID
    */
    private Integer monitorPointID;

    private static final long serialVersionUID = 1L;
}