package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCompanyViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoDeviceInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoDevicePageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoDeviceWithSensorIDInfo;
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

    List<VideoDeviceWithSensorIDInfo> selectListWithSensorIDBySensorIDList(List<Integer> sensorList);

    int batchInsert(List<VideoDeviceInfo> videoDeviceInfoList);

    IPage<VideoDevicePageInfo> queryPageByCondition(Page<VideoDevicePageInfo> page, String deviceSerial,
                                                    Boolean deviceStatus, Boolean allocationStatus,
                                                    Integer ownedCompanyID, Integer projectID,
                                                    Date begin, Date end);
    List<VideoDeviceInfoV1> queryListByCondition(List<String> deviceSerialList);

    Integer batchUpdate(List<VideoDeviceInfoV2> updateVideoList);

    List<VideoDeviceInfoV4> selectByIdList(List<Integer> videoIDList);

    Integer batchUpdateCompanyAndProject(List<VideoDeviceInfoV4> videoDeviceInfoV4List, Integer companyID);

    List<VideoDeviceInfoV1> queryListByDeviceSerialListAndCompanyID(List<String> deviceSerialList, Integer companyID);

    void batchUpdateDeviceStatus(List<TbVideoDevice> tbVideoDevices);
}
