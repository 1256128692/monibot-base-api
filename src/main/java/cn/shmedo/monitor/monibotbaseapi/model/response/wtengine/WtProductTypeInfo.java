package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-26 09:57
 */
@Data
@Builder
public class WtProductTypeInfo {
    private Integer productID;
    private String productName;
}
