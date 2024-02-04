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
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  Cmd {

        private String deviceToken;
        private Integer cmdID;
        private String exValue;
        private Collection<Param> paList;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Param {

            private Integer paID;
            private String paValue;
            private Integer paSetType;

        }
    }


    /**
     * 透传指令
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RawCmd {

        private String deviceToken;
        private String cmdContent;

    }
//    public record RawCmd(String deviceToken, String cmdContent) {
//
//    }

}

    
    