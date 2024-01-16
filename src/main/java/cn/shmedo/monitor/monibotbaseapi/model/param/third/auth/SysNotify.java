package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.util.Collection;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysNotify {

    private Integer companyID;

    Collection<Notify> notifyList;

    private Collection<Integer> notifyUsers;

    public record Notify(Type type, String name, String content, Status status,
                         @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+0") Date time) {}

    @Getter
    @AllArgsConstructor
    public enum Status {

        @JsonProperty("0")
        UNREAD(0, "未读"),

        @JsonProperty("1")
        READ(1, "已读"),

        @JsonProperty("8")
        TODO(2, "待办"),

        ;

        @JsonValue
        private final Integer code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        //1.报警 2.事件 3.信息
        @JsonProperty("1")
        ALARM(1, "报警"),

        @JsonProperty("2")
        EVENT(2, "事件"),

        @JsonProperty("3")
        INFO(3, "信息"),

        ;

        @JsonValue
        private final Integer code;
        private final String desc;
    }
}