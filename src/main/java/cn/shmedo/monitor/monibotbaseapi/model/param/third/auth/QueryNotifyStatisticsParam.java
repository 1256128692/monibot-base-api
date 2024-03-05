package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class QueryNotifyStatisticsParam {

    @NotNull
    private Integer companyID;
    @JsonIgnore
    private Integer userID;
    /** 1.报警 2.事件 3.工单 */
    private Integer type;
    private Integer serviceID;
    @JsonProperty("queryKey")
    private String queryCode;
    @Range(min = 0, max = 1, message = "已读/未读状态 0.未读 1.已读")
    private Integer status;
    private Date begin;
    private Date end;
}
