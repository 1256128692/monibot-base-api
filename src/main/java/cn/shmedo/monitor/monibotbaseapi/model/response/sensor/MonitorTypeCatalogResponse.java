package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 监测类型下拉查询 响应体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTypeCatalogResponse extends TbMonitorType {

    /**
     * 扩展字段
     */
    private List<TbMonitorTypeField> exFields;

    /**
     * 参数
     */
    private List<TbParameter> paramFields;
}

    
    