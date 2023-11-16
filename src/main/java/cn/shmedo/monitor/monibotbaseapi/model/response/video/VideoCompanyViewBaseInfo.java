package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 14:23
 */
@Data
public class VideoCompanyViewBaseInfo {
    private Integer videoDeviceID;
    private Boolean deviceStatus;
    private String deviceSerial;
    private String deviceName;
    private String deviceType;
    private Integer accessChannelNum;
    private Integer accessPlatform;
    private Integer accessProtocol;
    private Integer companyID;
    private Integer projectID;
    private Integer storageType;
    private Boolean captureStatus;
    private Boolean allocationStatus;
    @JsonIgnore
    private String channelDesc;

    @JsonProperty("deviceChannel")
    public List<Integer> deviceChannel() {
        return Optional.ofNullable(channelDesc).filter(ObjectUtil::isNotEmpty).map(u -> u.split(",")).map(Arrays::stream)
                .map(u -> u.filter(ObjectUtil::isNotEmpty).map(Integer::parseInt).sorted().toList()).orElse(List.of());
    }
}
