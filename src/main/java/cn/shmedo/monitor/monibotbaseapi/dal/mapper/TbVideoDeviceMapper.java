package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoCompanyViewBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoProjectViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.VideoDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItem4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCompanyViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoDevicePageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoProjectViewBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:53
 */
public interface TbVideoDeviceMapper extends BaseMapper<TbVideoDevice> {
    List<VideoCompanyViewBaseInfo> selectVideoCompanyViewBaseInfo(@Param("param") QueryVideoCompanyViewBaseInfoParam param);

    List<VideoProjectViewBaseInfo> selectVideoProjectViewBaseInfo(@Param("param") QueryVideoProjectViewBaseInfo param);


    int batchInsert(List<VideoDeviceInfo> videoDeviceInfoList);

    IPage<VideoDevicePageInfo> queryPageByCondition(Page<VideoDevicePageInfo> page, String deviceSerial,
                                                    Boolean deviceStatus, Boolean allocationStatus,
                                                    Integer ownedCompanyID, Integer projectID,
                                                    Date begin, Date end);



}
