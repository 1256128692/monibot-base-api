package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 监测类型下拉查询 响应体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTypeCatalogResponse extends TbMonitorType {

    public static <T extends TbMonitorType> MonitorTypeCatalogResponse valueOf(T t) {
        MonitorTypeCatalogResponse response = new MonitorTypeCatalogResponse();
        response.setID(t.getID());
        response.setMonitorType(t.getMonitorType());
        response.setTypeName(t.getTypeName());
        response.setTypeAlias(t.getTypeAlias());
        response.setDisplayOrder(t.getDisplayOrder());
        response.setMultiSensor(t.getMultiSensor());
        response.setApiDataSource(t.getApiDataSource());
        response.setCreateType(t.getCreateType());
        response.setCompanyID(t.getCompanyID());
        response.setExValues(t.getExValues());
        return response;
    }
}

    
    