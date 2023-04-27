package cn.shmedo.monitor.monibotbaseapi.model.response.third;

import cn.shmedo.iot.entity.api.iot.ProductType;
import lombok.Data;

/**
 * 设备基本信息
 *
 * @author Chengfs on 2023/4/27
 */
@Data
public class DeviceBaseInfo {

    /**
     * 设备SN
     */
    private String deviceToken;

    /**
     * 产品ID
     */
    private Integer productID;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品类型
     */
    private ProductType productType;

    public void setProductType(String productType) {
        this.productType = ProductType.valueOfEngName(productType);
    }
}