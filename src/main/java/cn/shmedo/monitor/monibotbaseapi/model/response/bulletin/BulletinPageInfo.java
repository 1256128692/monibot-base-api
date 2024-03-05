package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

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
    private Integer status;
    private Date createTime;
    private Boolean topMost;
}
