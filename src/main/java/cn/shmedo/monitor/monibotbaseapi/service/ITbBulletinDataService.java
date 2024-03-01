package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.bulletin.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:42
 */
public interface ITbBulletinDataService extends IService<TbBulletinData> {
    void addBulletinData(AddBulletinDataParam param, Integer userID);

    void updateBulletinData(UpdateBulletinData param, Integer userID);

    List<BulletinDataListInfo> queryBulletinList(QueryBulletinListParam param);

    PageUtil.Page<BulletinPageInfo> queryBulletinPage(QueryBulletinPageParam param);

    BulletinDetailInfo queryBulletinDetail(QueryBulletinDetailParam param);
}
