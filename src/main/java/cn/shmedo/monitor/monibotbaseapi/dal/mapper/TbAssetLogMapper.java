package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbAssetLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.asset.QueryAssetIOLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.asset.TbAssetLog4Web;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TbAssetLogMapper extends BaseMapper<TbAssetLog> {
    IPage<TbAssetLog4Web> queryAssetIOLogPage(Page<TbAssetLog4Web> page, QueryAssetIOLogPageParam pa);
}