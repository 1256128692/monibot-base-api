package cn.shmedo.monitor.monibotbaseapi.model.response.bulletin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 16:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BulletinDataListInfo extends BulletinDataBaseInfo {
    private Integer bulletinID;
}
