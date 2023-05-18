package cn.shmedo.monitor.monibotbaseapi.model.response.third;

import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceState;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Chengfs on 2023/5/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeviceStateResponse extends DeviceState {

    /**
     * 设备SN号
     */
    private String token;

    /**
     * 状态时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;
}