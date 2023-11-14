package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DeleteDeviceParam {

    private List<Integer> idList;
    /**
     * 是否保存原始数据
     */
    private Boolean saveData;

    /**
     * 公司ID
     */
    private Integer companyID;
}
