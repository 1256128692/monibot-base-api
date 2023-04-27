package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * 查询设备基本信息 请求体
 *
 * @author Chengfs on 2023/4/27
 */
@Data
@Builder
public class QueryDeviceBaseInfoParam {

    private Integer companyID;

    private Set<String> deviceTokens;

}