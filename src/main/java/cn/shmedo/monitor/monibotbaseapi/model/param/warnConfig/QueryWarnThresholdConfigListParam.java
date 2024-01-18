package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 10:03
 */
public class QueryWarnThresholdConfigListParam {
 Integer   projectID;
 Integer           platform;
 Integer   monitorItemID;
 Boolean           status;
 List<Integer>   monitorPointIDList;
 List<Integer> sensorIDList;
}
