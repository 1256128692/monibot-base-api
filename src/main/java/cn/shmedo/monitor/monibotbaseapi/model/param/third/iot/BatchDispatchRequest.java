package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * 批量指令下发 参数
 *
 * @author Chengfs on 2023/11/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDispatchRequest {

    private Integer taskID;
    private Integer userID;

    private List<Cmd> cmdList;

    private List<RawCmd> rawCmdList;

    /**
     * 通用指令
     */
    public record Cmd(String deviceToken, Integer cmdID, String exValue, Collection<Param> paList) {

        public Cmd(String deviceToken, Integer cmdID, Param... paList) {
            this(deviceToken, cmdID, null, List.of(paList));
        }

        /**
         * 通用指令参数
         */
        public record Param(Integer paID, String paValue, Integer paSetType) {

        }
    }



    /**
     * 透传指令
     */
    public record RawCmd(String deviceToken, String cmdContent) {

    }

}

    
    