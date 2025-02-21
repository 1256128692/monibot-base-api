package cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 13:40
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TbOtherDevice4Web extends TbOtherDevice {
    private String projectName;
    private String templateName;
    private String templateGroupName;
}
