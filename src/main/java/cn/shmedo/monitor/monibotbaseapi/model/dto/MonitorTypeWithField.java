package cn.shmedo.monitor.monibotbaseapi.model.dto;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Chengfs on 2023/4/7
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTypeWithField extends TbMonitorType {

    private List<TbMonitorTypeField> fieldList;
}

    
    