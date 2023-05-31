package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-11 15:01
 **/
@Data
public class MonitorItemV1 {
    private Integer itemID;
    private String itemName;
    private String itemAlias;
    private Integer monitorType;
    @JsonIgnore
    private Boolean enable;
    private String typeName;
    private String typeAlias;
    private Byte createType;
    List<MonitorTypeFieldV1> fieldList;

}
