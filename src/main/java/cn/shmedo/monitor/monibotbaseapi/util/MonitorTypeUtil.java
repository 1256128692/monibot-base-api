package cn.shmedo.monitor.monibotbaseapi.util;


public class MonitorTypeUtil {

    public static String getMeasurement(Integer monitorType, boolean raw, boolean avg) {
        StringBuilder result = new StringBuilder();
        result.append("tb_");
        if (raw) {
            result.append("raw_");
        }
        result.append(monitorType);
        if (avg) {
            result.append("_avg");
        }
        return result.toString();
    }
}
