package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

import cn.shmedo.monitor.monibotbaseapi.model.enums.BulletinPublishStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 17:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BulletinPageInfo extends BulletinDataListInfo {
    @JsonIgnore
    private Date updateTime;
    private Integer status;
    private Date createTime;

    @JsonProperty("updateTime")
    private Date updateTime() {
        return BulletinPublishStatus.PUBLISHED.getCode().equals(status) ? updateTime : null;
    }
}
