package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransferDeviceParam  {
    /**
     * 设备ID列表
     */
    private List<Integer> deviceIDList;
    /**
     * 公司ID
     */
    private Integer companyID;

    private Integer originalCompanyID;

}
