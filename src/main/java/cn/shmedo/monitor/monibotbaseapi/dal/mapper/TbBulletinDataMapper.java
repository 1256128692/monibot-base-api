package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.QueryBulletinListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.QueryBulletinPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.bulletin.BulletinDataListInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.bulletin.BulletinDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.bulletin.BulletinPageInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:41
 */
public interface TbBulletinDataMapper extends BaseMapper<TbBulletinData> {
    List<BulletinDataListInfo> selectBulletinList(QueryBulletinListParam param);

    IPage<BulletinPageInfo> selectBulletinPage(IPage<BulletinPageInfo> page, @Param("param") QueryBulletinPageParam param);

    BulletinDetailInfo selectBulletinDetail(Integer bulletinID);
}
