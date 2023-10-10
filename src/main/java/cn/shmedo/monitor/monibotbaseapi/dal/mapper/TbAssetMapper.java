package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbAsset;
import cn.shmedo.monitor.monibotbaseapi.model.param.asset.QueryAssetPageParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TbAssetMapper extends BaseMapper<TbAsset> {
    IPage<TbAsset> queryPage(Page<TbAsset> page, QueryAssetPageParam pa);
}