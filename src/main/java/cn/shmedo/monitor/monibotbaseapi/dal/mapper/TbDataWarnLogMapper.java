package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDataWarnPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnLatestInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnPageInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 14:38
 */
public interface TbDataWarnLogMapper extends BasicMapper<TbDataWarnLog> {
    IPage<DataWarnPageInfo> selectDataWarnPage(Page<DataWarnPageInfo> page, @Param("param") QueryDataWarnPageParam param);

    DataWarnLatestInfo selectDataWarnBaseInfoByID(Integer id);
}
