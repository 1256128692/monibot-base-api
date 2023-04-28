package cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice;

import lombok.Builder;
import lombok.Data;

/**
 * @author Chengfs on 2023/4/28
 */
@Data
@Builder
public class ProductSimple {

    private Integer productID;

    private String productName;
}