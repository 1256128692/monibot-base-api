package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-25 17:55
 */
@Data
@Builder
public class WtVideoTypeInfo {
    private String deviceTypeName;
    private String deviceTypeToken;
}
