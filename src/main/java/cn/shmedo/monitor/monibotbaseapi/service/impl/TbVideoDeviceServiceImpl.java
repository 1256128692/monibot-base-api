package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoCompanyViewBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoProjectViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryYsVideoDeviceInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryYsVideoPlayBackParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCompanyViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoProjectViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoProjectViewPointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoProjectViewSubGroupInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbVideoDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 14:20
 */
@Service
public class TbVideoDeviceServiceImpl extends ServiceImpl<TbVideoDeviceMapper, TbVideoDevice> implements ITbVideoDeviceService {
    @Override
    public List<VideoCompanyViewBaseInfo> queryVideoCompanyViewBaseInfo(QueryVideoCompanyViewBaseInfoParam param) {
        return this.baseMapper.selectVideoCompanyViewBaseInfo(param);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public List<VideoProjectViewBaseInfo> queryVideoProjectViewBaseInfo(QueryVideoProjectViewBaseInfo param) {
        return this.baseMapper.selectVideoProjectViewBaseInfo(param).stream().peek(u -> u.getMonitorGroupDataList().stream()
                .map(VideoProjectViewSubGroupInfo::getMonitorPointDataList).flatMap(Collection::stream)
                .peek(VideoProjectViewPointInfo::afterProperties).toList()).toList();
    }

    @Override
    public Object queryYsVideoDeviceInfo(QueryYsVideoDeviceInfoParam param) {
        return null;
    }

    @Override
    public Object queryYsVideoPlayBack(QueryYsVideoPlayBackParam param) {
        return null;
    }
}
