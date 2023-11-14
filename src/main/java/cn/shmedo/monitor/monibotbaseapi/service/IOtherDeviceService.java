package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.AddOtherDeviceBatchParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.QueryOtherDevicePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.QueryOtherDeviceWithPropertyParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.UpdateOtherDeviceParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.TbOtherDevice4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.TbOtherDeviceWithProperty;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-27 17:50
 **/
public interface IOtherDeviceService extends IService<TbOtherDevice> {
    void addOtherDeviceBatch(AddOtherDeviceBatchParam pa, Integer subjectID);

    void deleteOtherDevice(List<Integer> deviceIDList);

    PageUtil.Page<TbOtherDevice4Web> queryOtherDevicePage(QueryOtherDevicePageParam pa);

    TbOtherDeviceWithProperty queryOtherDeviceWithProperty(QueryOtherDeviceWithPropertyParam pa);

    void updateOtherDevice(UpdateOtherDeviceParam pa, Integer subjectID);
}
