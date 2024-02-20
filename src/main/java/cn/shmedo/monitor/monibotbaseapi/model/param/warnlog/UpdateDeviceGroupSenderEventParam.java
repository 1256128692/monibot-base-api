package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-18 15:59
 */
@Data
public class UpdateDeviceGroupSenderEventParam {
    @NotEmpty(message = "设备sn不能为空")
    private String deviceToken;
    /**
     * 更新后设备在绑的工程IDList,服务根据推送配置将设备在线/离线数据推送到指定工程(即设备在绑工程)。
     * <p>
     * 1、如果{@code projectIDList}为{@link Collections#emptyList()},表示该设备已经不推送到任何一个工程(可能是设备已删除),此时需要删除该设备全部的报警记录;<br>
     * 2、否则该设备可能<b>解绑</b>了部分工程,此时需要删除掉<b>解绑工程</b>的报警数据。
     * </p>
     */
    private List<Integer> projectIDList;
}
