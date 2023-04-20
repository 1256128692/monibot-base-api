package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWorkOrder;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderWarnDetail;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-19 09:54
 */
public interface ITbWorkOrderService extends IService<TbWorkOrder> {
    PageUtil.Page<WtWorkOrderInfo> queryWorkOrderPage(QueryWorkOrderPageParam param);

    WtWorkOrderWarnDetail queryWarnDetail(QueryWorkOrderWarnDetailParam param);

    WtWorkOrderStatisticsInfo queryWorkOrderStatistics(QueryWorkOrderStatisticsParam param);

    WtWorkOrderDetailInfo queryWorkOrderDetail(QueryWorkOrderWarnDetailParam param);
}
