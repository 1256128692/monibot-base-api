package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWorkOrder;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.AddWarnLogBindWarnOrderParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderStatisticsInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-19 09:53
 */
@Mapper
public interface TbWorkOrderMapper extends BaseMapper<TbWorkOrder> {
    IPage<WtWorkOrderInfo> queryWorkOrderPage(IPage<?> page, @Param("param") QueryWorkOrderPageParam param);

    WtWorkOrderStatisticsInfo queryWorkOrderStatistics(@Param("param") QueryWorkOrderStatisticsParam param);

    WtWorkOrderDetailInfo queryWorkOrderDetail(@Param("param") QueryWorkOrderWarnDetailParam param);

    int insertByCondition(AddWarnLogBindWarnOrderParam param);
}
