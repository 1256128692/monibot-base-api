package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-11 15:28
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class TbMonitorTypeFieldWithItemID extends TbMonitorTypeField {
    private Integer itemID;
    private String alias;
}
