package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetHouse;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.asset.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.asset.TbAsset4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.asset.TbAssetLog4Web;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 17:08
 **/
public interface IAssetService extends IService<TbAsset> {
    void addAssetHouse(AddAssetHouseParam pa, Integer subjectID);

    void updateAssetHouse(UpdateAssetHouseParam pa, Integer subjectID);

    void deleteAssetHouse(List<Integer> houseIDList);

    List<TbAssetHouse> queryAssetHouseList(Integer companyID);

    void ioAsset(IOAssetParam pa, CurrentSubject currentSubject);

    PageUtil.Page<TbAssetLog4Web> queryAssetIOLogPage(QueryAssetIOLogPageParam pa);

    PageUtil.Page<TbAsset4Web> queryAssetPage(QueryAssetPageParam pa);
}
