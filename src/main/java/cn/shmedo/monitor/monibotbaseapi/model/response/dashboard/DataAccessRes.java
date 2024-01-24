package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author wuxl
 * @Date 2024/1/24 13:29
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.dashboard
 * @ClassName: DataAccessRes
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class DataAccessRes {
    private String[] platformNameList;
    private String[] protocolNameList;
    private int deviceCount;
    private int increaseInThePastYear;
    private int increaseInThePastMonth;
}
