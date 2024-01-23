package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbUserFollowMonitorPoint implements Serializable {
    private Integer ID;

    private Integer userID;

    private Integer monitorPointID;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}