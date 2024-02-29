package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.AddBulletinDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.UpdateBulletinData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:42
 */
public interface ITbBulletinDataService extends IService<TbBulletinData> {
    void addBulletinData(AddBulletinDataParam param, Integer userID);

    void updateBulletinData(UpdateBulletinData param, Integer userID);
}
