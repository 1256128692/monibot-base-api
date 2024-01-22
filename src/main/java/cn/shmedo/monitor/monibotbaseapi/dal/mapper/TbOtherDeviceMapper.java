package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.AddOtherDeviceItem;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.QueryOtherDevicePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.OtherDeviceCountInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.TbOtherDevice4Web;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface TbOtherDeviceMapper extends BasicMapper<TbOtherDevice> {
    IPage<TbOtherDevice4Web> queryOtherDevicePage(Page<TbOtherDevice4Web> page, QueryOtherDevicePageParam pa);

    int countExist(List<AddOtherDeviceItem> list);

    List<OtherDeviceCountInfo> queryCountByProjectIDList(List<Integer> projectIDList);
}