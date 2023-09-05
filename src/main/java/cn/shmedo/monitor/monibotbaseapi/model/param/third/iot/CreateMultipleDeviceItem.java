package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMultipleDeviceItem {
    /**
     * 产品ID
     */
    private Integer productID;
    /**
     * 设备SN,最大30
     */
    private String deviceToken;
    /**
     * 产品名称最大30,为空则与deviceToken一致
     */
    private String deviceName;

}
