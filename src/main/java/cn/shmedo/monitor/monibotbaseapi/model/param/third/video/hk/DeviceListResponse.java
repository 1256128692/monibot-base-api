package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk;

import lombok.Data;

import java.util.List;

@Data
public class DeviceListResponse {


    private int pageNo;
    private int pageSize;
    private int totalPage;
    private int total;
    private List<HkDeviceStatusInfo> list;

}

