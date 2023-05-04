package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.YsTokenDao;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsCode;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsTokenInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.QueryVideoBaseInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.QueryVideoBaseInfoResult;
import cn.shmedo.monitor.monibotbaseapi.service.VideoService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ys.YsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final YsService ysService;
    private final FileConfig fileConfig;
    private final YsTokenDao ysTokenDao;


    /**
     * 获取萤石云TOKEN，如果REDIS中没有，则从接口中获取
     *
     * @return
     */
    public String getYsToken() {
        String token = ysTokenDao.getToken();
        if (StrUtil.isNotBlank(token)) {
            return token;
        } else {
            YsResultWrapper<YsTokenInfo> tokenInfoYsResultWrapper =
                    ysService.getToken(fileConfig.getYsAppKey(), fileConfig.getYsSecret());
            if (tokenInfoYsResultWrapper == null || StrUtil.isBlank(tokenInfoYsResultWrapper.getCode())
                    || tokenInfoYsResultWrapper.getData() == null || tokenInfoYsResultWrapper.getData().getAccessToken() == null
                    || (!YsCode.SUCCESS.equals(tokenInfoYsResultWrapper.getCode()))) {
                throw new RuntimeException("ys service error:" + tokenInfoYsResultWrapper);
            }
            String httpToken = tokenInfoYsResultWrapper.getData().getAccessToken();
            ysTokenDao.setToken(httpToken);
            return httpToken;
        }
    }

    @Override
    public QueryVideoBaseInfoResult queryVideoBaseInfo(QueryVideoBaseInfoParam param) {
        String ysToken = getYsToken();
        YsResultWrapper<YsDeviceInfo> deviceInfoWrapper = ysService.getDeviceInfo(ysToken, param.getVideoSn());
        if (deviceInfoWrapper == null || StrUtil.isBlank(deviceInfoWrapper.getCode())
                || deviceInfoWrapper.getData() == null ||
                (!YsCode.SUCCESS.equals(deviceInfoWrapper.getCode()))) {
            throw new RuntimeException("ys service error:" + deviceInfoWrapper);
        }
        return QueryVideoBaseInfoResult.valueOf(deviceInfoWrapper.getData());
    }
}
