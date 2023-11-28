package cn.shmedo.monitor.monibotbaseapi.model.response.eigenValue;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.MonitorPointBaseInfoV2;
import lombok.Data;

import java.util.List;

@Data
public class EigenValueInfoV1 extends TbEigenValue {

    private String scopeStr;

    private String monitorTypeFieldName;

    private String monitorTypeFieldToken;

    private String monitorItemName;

    private String engUnit;

    private String chnUnit;

    private List<MonitorPointBaseInfoV2> monitorPointList;

}
