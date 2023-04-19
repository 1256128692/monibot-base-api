package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWorkOrderMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWorkOrder;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.workorder.WtWorkOrderWarnDetail;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWorkOrderService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-19 09:54
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbWorkOrderServiceImpl extends ServiceImpl<TbWorkOrderMapper, TbWorkOrder> implements ITbWorkOrderService {
    private final TbWarnLogMapper tbWarnLogMapper;

    @Override
    public PageUtil.Page<WtWorkOrderInfo> queryWorkOrderPage(QueryWorkOrderPageParam param) {
        IPage<WtWorkOrderInfo> page = this.baseMapper.queryWorkOrderPage(
                new Page<>(param.getCurrentPage(), param.getPageSize()), param);
        return new PageUtil.Page<>(page.getPages(), page.getRecords(), page.getTotal());
    }

    @Override
    public WtWorkOrderWarnDetail queryWarnDetail(QueryWorkOrderWarnDetailParam param) {
        return this.tbWarnLogMapper.queryWorkOrderWarnDetail(param);
    }

    @Override
    public WtWorkOrderStatisticsInfo queryWorkOrderStatistics(QueryWorkOrderStatisticsParam param) {
       return this.baseMapper.selectWorkOrderStatistics(param);
    }
}
