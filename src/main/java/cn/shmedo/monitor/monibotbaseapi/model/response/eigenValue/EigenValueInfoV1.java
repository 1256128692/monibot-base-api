package cn.shmedo.monitor.monibotbaseapi.model.response.eigenValue;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import lombok.Data;

@Data
public class EigenValueInfoV1 extends TbEigenValue {

    private String scopeStr;

    private String monitorTypeFieldName;

    private String engUnit;

    private String chnUnit;

}
