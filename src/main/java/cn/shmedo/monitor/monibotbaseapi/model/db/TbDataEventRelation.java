package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbDataEventRelation implements Serializable {
    private Integer id;

    /**
    * 大事件ID
    */
    private Integer eventID;

    /**
    * 监测项目ID
    */
    private Integer monitorItemID;

    private static final long serialVersionUID = 1L;
}