package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-11 15:28
 **/
@Data
public class TbMonitorTypeFieldWithItemID extends TbMonitorTypeField {
    private Integer itemID;
}
