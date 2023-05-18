package cn.shmedo.monitor.monibotbaseapi.model.response.monitorType;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataUnit;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorItemFieldResponse {

    /**
     * 监测类型属性字段列表
     */
    private List<TbMonitorTypeField> fieldList;

    /**
     * 监测类型属性字段单位列表
     */
    private List<TbDataUnit> dataUnitList;


}
