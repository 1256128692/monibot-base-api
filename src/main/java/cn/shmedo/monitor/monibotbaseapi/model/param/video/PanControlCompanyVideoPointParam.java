package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoCompanyViewBaseInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 10:13
 */
@Data
public class PanControlCompanyVideoPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private final Integer companyID;
    @NotNull(message = "视频设备ID不能为空")
    private Integer videoDeviceID;
    @NotNull(message = "视频设备通道号不能为空")
    private Integer deviceChannel;
    @NotNull(message = "方向不能为空")
    @Range(max = 16, message = "方向 0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距，16-自动控制")
    private Integer direction;
    @JsonIgnore
    private VideoCompanyViewBaseInfo baseInfo;

    @Override
    public ResultWrapper validate() {
        QueryVideoCompanyViewBaseInfoParam param = new QueryVideoCompanyViewBaseInfoParam();
        param.setCompanyID(companyID);
        param.setVideoDeviceID(videoDeviceID);
        final List<VideoCompanyViewBaseInfo> videoCompanyViewBaseInfos = ContextHolder.getBean(TbVideoDeviceMapper.class)
                .selectVideoCompanyViewBaseInfo(param);
        if (CollUtil.isEmpty(videoCompanyViewBaseInfos)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不存在!");
        }
        this.baseInfo = videoCompanyViewBaseInfos.get(0);
        if (!this.baseInfo.deviceChannel().contains(deviceChannel)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不存在该通道号!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
