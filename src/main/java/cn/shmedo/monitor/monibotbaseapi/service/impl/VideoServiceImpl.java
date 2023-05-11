package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorFileMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.redis.YsTokenDao;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsCode;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsTokenInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.HistoryLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.QueryVideoBaseInfoResult;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointPictureInfo;
import cn.shmedo.monitor.monibotbaseapi.service.VideoService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ys.YsService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.device.ys.YsUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final YsService ysService;
    private final FileConfig fileConfig;
    private final YsTokenDao ysTokenDao;

    private final TbSensorFileMapper sensorFileMapper;

    private final FileService fileService;

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


    @Override
    public List<VideoMonitorPointLiveInfo> queryVideoMonitorPointLiveInfo(QueryVideoMonitorPointLiveInfoParam pa) {

        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();

        String ysToken = getYsToken();
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {
            liveInfos.forEach( item -> {
                String baseUrl = YsUtil.getEzOpenAddress(item.getSeqNo(), false, item.getYsChannelNo());
                String hdUrl = YsUtil.getEzOpenAddress(item.getSeqNo(), true, item.getYsChannelNo());
                item.setBaseUrl(baseUrl);
                item.setHdUrl(hdUrl);
                item.setYsToken(ysToken);
            });

            return liveInfos;
        }
        return Collections.emptyList();
    }

    @Override
    public HistoryLiveInfo queryVideoMonitorPointHistoryLiveInfo(QueryVideoMonitorPointHistoryLiveInfoParam pa) {
        HistoryLiveInfo vo = new HistoryLiveInfo();
        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {
            String ysToken = getYsToken();
            String ezOpenHistoryAddress = YsUtil.getEzOpenHistoryAddress(liveInfos.get(0).getSeqNo(), liveInfos.get(0).getYsChannelNo(), pa.getBeginTime(),
                    pa.getEndTime());
            vo.setHistoryLiveAddress(ezOpenHistoryAddress);
            vo.setYsToken(ysToken);
        }

        return vo;
    }


    @Override
    public ResultWrapper<Object> panControlVideoPoint(PanControlVideoPointParam pa) {

        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {

            String ysToken = getYsToken();
            YsResultWrapper ysResultWrapper = ysService.startPtz(ysToken, liveInfos.get(0).getSeqNo(), Integer.valueOf(liveInfos.get(0).getYsChannelNo()), pa.getDirection(),
                    DefaultConstant.YS_DEFAULT_SPEED);
            ysService.stopPtz(ysToken, liveInfos.get(0).getSeqNo(), Integer.valueOf(liveInfos.get(0).getYsChannelNo()), pa.getDirection());
            if (!ysResultWrapper.callSuccess()) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR, ysResultWrapper.getMsg());
            }
        }
        return ResultWrapper.successWithNothing();
    }



    @Override
    public List<VideoMonitorPointPictureInfo> queryVideoMonitorPointPictureInfo(QueryVideoMonitorPointPictureInfoParam pa) {

        List<VideoMonitorPointLiveInfo> liveInfos = pa.getLiveInfos();
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {
            List<VideoMonitorPointPictureInfo> list = sensorFileMapper.selectListByIDAndTime(liveInfos.get(0).getSensorID(), pa.getBeginTime() , pa.getEndTime());
            List<String> filePathList = list.stream().map(VideoMonitorPointPictureInfo::getFilePath).collect(Collectors.toList());
            List<FileInfoResponse> fileUrlList = fileService.getFileUrlList(filePathList, liveInfos.get(0).getCompanyID());
            if (!CollectionUtil.isNullOrEmpty(fileUrlList)){
                list.forEach(item -> {
                    FileInfoResponse fileInfoResponse = fileUrlList.stream().filter(pojo -> pojo.getFilePath().equals(item.getFilePath())).findFirst().orElse(null);
                    assert fileInfoResponse != null;
                    item.setFilePath(fileInfoResponse.getAbsolutePath());
                });
            }

            return list.stream()
                    .sorted(Comparator.comparing(VideoMonitorPointPictureInfo::getUploadTime).reversed())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
