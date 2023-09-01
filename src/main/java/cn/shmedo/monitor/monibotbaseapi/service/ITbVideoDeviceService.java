package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoCompanyViewBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoProjectViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryYsVideoDeviceInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryYsVideoPlayBackParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCompanyViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoProjectViewBaseInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 14:19
 */
public interface ITbVideoDeviceService extends IService<TbVideoDevice> {
    List<VideoCompanyViewBaseInfo> queryVideoCompanyViewBaseInfo(QueryVideoCompanyViewBaseInfoParam param);

    List<VideoProjectViewBaseInfo> queryVideoProjectViewBaseInfo(QueryVideoProjectViewBaseInfo param);

    Object queryYsVideoDeviceInfo(QueryYsVideoDeviceInfoParam param);

    Object queryYsVideoPlayBack(QueryYsVideoPlayBackParam param);
}
