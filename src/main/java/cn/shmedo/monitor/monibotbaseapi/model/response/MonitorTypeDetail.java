package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-29 09:52
 **/
@Data
public class MonitorTypeDetail extends TbMonitorType {
    private List<TbMonitorTypeField> fieldList;
    private List<TbMonitorTypeField> class3FieldList;
    private List<TbMonitorTypeTemplate4Web> templateList;
}
