package cn.shmedo.monitor.monibotbaseapi.model.dto.device;

import lombok.Data;

/**
 * @author Chengfs on 2023/11/27
 */
@Data
public class TokenAndMsgID {

    private String deviceToken;
    private String msgID;
}