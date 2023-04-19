package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnDetailInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warn.WtWarnLogInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbWarnLogServiceImpl extends ServiceImpl<TbWarnLogMapper, TbWarnLog> implements ITbWarnLogService {
    private final TbProjectInfoMapper tbProjectInfoMapper;

    @Override
    public PageUtil.Page<WtWarnLogInfo> queryByPage(QueryWtWarnLogPageParam param) {
        List<Integer> projectIDList = tbProjectInfoMapper.selectList(
                new LambdaQueryWrapper<TbProjectInfo>().eq(TbProjectInfo::getCompanyID, param.getCompanyID())
                        .select(TbProjectInfo::getID)).stream().map(TbProjectInfo::getID).toList();
        if (CollectionUtil.isNullOrEmpty(projectIDList)) {
            return PageUtil.Page.empty();
        }
        // 实时记录是测点报警状态最新一条报警数据，并不是指当天的；历史记录是除了最新报警状态的报警数据其他触发报警状态的报警数据
        Integer queryType = param.getQueryType();
        Integer pageSize = param.getPageSize() == 0 ? 1 : param.getPageSize();
        List<WtWarnLogInfo> wtWarnLogInfos;
        Long totalCount = 0L;
        if (queryType == 1) {
            wtWarnLogInfos = this.baseMapper.queryCurrentRecords(param, projectIDList);
            totalCount = this.baseMapper.queryCurrentRecordCount(param, projectIDList);
        } else if (queryType == 2) {
            IPage<WtWarnLogInfo> page = this.baseMapper.queryHistoryRecords(
                    new Page<>(param.getCurrentPage(), pageSize), param, projectIDList);
            wtWarnLogInfos = page.getRecords();
            totalCount = page.getTotal();
        } else {
            wtWarnLogInfos = new ArrayList<>();
        }
        return new PageUtil.Page<>(totalCount / pageSize + 1, wtWarnLogInfos, totalCount);
    }

    @Override
    public WtWarnDetailInfo queryDetail(QueryWtWarnDetailParam param) {
        return this.baseMapper.queryWarnDetail(param.getWarnID());
    }
}
