package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbUserLogMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.userLog.QueryUserOperationLogParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.userLog.QueryUserLogResult;
import cn.shmedo.monitor.monibotbaseapi.service.ITbUserLogService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/2/20
 */
@Service
public class TbUserLogServiceImpl extends ServiceImpl<TbUserLogMapper, TbUserLog> implements ITbUserLogService {

    @Override
    public PageUtil.Page<QueryUserLogResult> queryUserOperationLog(QueryUserOperationLogParameter pa) {
        LambdaQueryWrapper<TbUserLog> query = Wrappers.<TbUserLog>lambdaQuery()
                .eq(TbUserLog::getCompanyID, pa.getCompanyID())
                .ge(TbUserLog::getOperationDate, pa.getBegin())
                .le(TbUserLog::getOperationDate, pa.getEnd())
                .orderByDesc(TbUserLog::getOperationDate);

        Optional.ofNullable(pa.getUserID()).ifPresent(e -> query.eq(TbUserLog::getUserID, e));
        Optional.ofNullable(pa.getOperationType()).filter(e -> !e.isBlank()).ifPresent(e -> query.eq(TbUserLog::getOperationProperty, e));
        Optional.ofNullable(pa.getModelName()).filter(e -> !e.isBlank()).ifPresent(e -> query.like(TbUserLog::getModuleName, e));
        Optional.ofNullable(pa.getOperationName()).filter(e -> !e.isBlank()).ifPresent(e -> query.like(TbUserLog::getOperationName, e));

        List<QueryUserLogResult> list = baseMapper.selectList(query).stream()
                .collect(Collectors.groupingBy(ul -> ul.getOperationDate().toLocalDate()))
                .entrySet().stream().map(e -> new QueryUserLogResult(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(QueryUserLogResult::getDate).reversed())
                .collect(Collectors.toList());

        return PageUtil.page(list, pa.getPageSize(), pa.getCurrentPage());
    }
}