package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoCompanyViewBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCompanyViewBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbVideoDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 14:20
 */
@Service
public class TbVideoDeviceServiceImpl extends ServiceImpl<TbVideoDeviceMapper, TbVideoDevice> implements ITbVideoDeviceService {
    @Override
    public List<VideoCompanyViewBaseInfo> queryVideoCompanyViewBaseInfo(QueryVideoCompanyViewBaseInfoParam param) {
        LambdaQueryWrapper<TbVideoDevice> wrapper = new LambdaQueryWrapper<TbVideoDevice>().eq(TbVideoDevice::getCompanyID, param.getCompanyID());
        Optional.ofNullable(param.getDeviceSerial()).ifPresent(u -> wrapper.like(TbVideoDevice::getDeviceSerial, u));
        Optional.ofNullable(param.getStatus()).filter(u -> u != 0).ifPresent(u -> wrapper.eq(TbVideoDevice::getDeviceStatus, u == 1));
        List<TbVideoDevice> list = this.list(wrapper);
//        list.stream().filter(u -> u.getAccessPlatform() == 0).
        return null;
    }
}
