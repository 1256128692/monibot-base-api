package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-24 17:01
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class TbMonitorType4web extends TbMonitorType {
    private Integer datasourceCount;
    private List<TbMonitorTypeField> fieldList;
}
