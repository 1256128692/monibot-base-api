package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoCompanyViewBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoProjectViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.VideoDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCompanyViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoProjectViewBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:53
 */
public interface TbVideoDeviceMapper extends BaseMapper<TbVideoDevice> {
    List<VideoCompanyViewBaseInfo> selectVideoCompanyViewBaseInfo(@Param("param") QueryVideoCompanyViewBaseInfoParam param);

    List<VideoProjectViewBaseInfo> selectVideoProjectViewBaseInfo(@Param("param") QueryVideoProjectViewBaseInfo param);


    int batchInsert(List<VideoDeviceInfo> videoDeviceInfoList);
}
