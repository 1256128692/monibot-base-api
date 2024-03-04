package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.cache.ProjectInfoCache;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint.CheckPointGroupSimple;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint.CheckPointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint.CheckPointSimple;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint.*;
import cn.shmedo.monitor.monibotbaseapi.service.CheckPointService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.TransferUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckPointServiceImpl extends ServiceImpl<TbCheckPointMapper, TbCheckPoint> implements CheckPointService {

    private static final String XJ = "XJ";
    private final TbCheckTaskPointMapper taskPointMapper;
    private final TbCheckPointGroupMapper groupMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final RedisService monitorRedisService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(AddCheckPointRequest request) {
        TbCheckPoint entity = request.toTbCheckPoint();
        entity.setSerialNumber(generateSerialNumber());
        this.save(entity);
        return entity.getID();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateCheckPointRequest request) {
        this.updateById(request.toEntity());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdate(BatchUpdateCheckPointRequest request) {
        Optional.ofNullable(request.getGroupID())
                .ifPresent(groupID -> this.update(Wrappers.lambdaUpdate(TbCheckPoint.class)
                        .set(TbCheckPoint::getGroupID, null)
                        .eq(TbCheckPoint::getGroupID, request.getGroupID())));
        this.updateBatchById(request.toEntitys());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeleteCheckPointRequest request) {
        // 任务状态为未开始的点，直接从任务中移除
        Optional.of(request.getPointStatus().stream()
                        .filter(e -> CheckTaskStatus.UN_START.getCode().equals(e.getT3()))
                        .map(Tuple2::getT1).distinct().toList())
                .filter(e -> !e.isEmpty())
                .ifPresent(taskPointMapper::deleteBatchIds);
        this.removeByIds(request.getIdList());
    }

    @Override
    public PageUtil.Page<CheckPointSimple> page(QueryCheckPointPageRequest request) {
        Page<CheckPointSimple> page = baseMapper.page(request, new Page<>(request.getCurrentPage(), request.getPageSize()));
        return new PageUtil.Page<>(page.getPages(), page.getRecords(), page.getTotal());
    }

    @Override
    public List<CheckPointSimple> list(QueryCheckPointListRequest request) {
        return baseMapper.list(request);
    }

    @Override
    public CheckPointInfo single(QueryCheckPointRequest request) {
        TbCheckPoint entity = request.getEntity();
        CheckPointInfo result = CheckPointInfo.valueOf(entity);

        ProjectInfoCache project = monitorRedisService.get(RedisKeys.PROJECT_KEY,
                entity.getProjectID().toString(), ProjectInfoCache.class);
        Optional.ofNullable(project).ifPresent(e -> result.setProjectName(e.getProjectName()));
        Optional.ofNullable(entity.getGroupID()).flatMap(gid -> Optional.ofNullable(groupMapper
                .selectOne(Wrappers.<TbCheckPointGroup>lambdaQuery().eq(TbCheckPointGroup::getID, gid)
                        .select(TbCheckPointGroup::getName)))).ifPresent(g -> result.setGroupName(g.getName()));
        TransferUtil.INSTANCE.getUserNameDict(List.of(result.getCreateUserID(), result.getUpdateUserID()))
                .ifPresent(dict -> {
                    result.setCreateUserName(dict.get(result.getCreateUserID()));
                    result.setUpdateUserName(dict.get(result.getUpdateUserID()));
                });
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveGroup(AddCheckPointGroupRequest request) {
        TbCheckPointGroup entity = request.toEntity();
        groupMapper.insert(entity);
        return entity.getID();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroup(UpdateCheckPointGroupRequest request) {
        groupMapper.updateById(request.toEntity());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(DeleteCheckPointGroupRequest request) {
        this.update(Wrappers.<TbCheckPoint>lambdaUpdate()
                .in(TbCheckPoint::getGroupID, request.getIdList())
                .set(TbCheckPoint::getGroupID, null)
                .set(TbCheckPoint::getUpdateUserID, CurrentSubjectHolder.getCurrentSubject().getSubjectID()));
        groupMapper.deleteBatchIds(request.getIdList());
    }

    @Override
    public List<CheckPointGroupSimple> listGroup(QueryCheckPointGroupListRequest request) {
        return groupMapper.list(request);
    }

    /**
     * 巡检点编号由首字母XJ、当前时间（年）、5位序列号（00001开始)组成
     *
     * @return 巡检点编号
     */
    protected synchronized String generateSerialNumber() {
        String prefix = XJ + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        Integer number = baseMapper.lastSerialNumber(prefix);
        return prefix + NumberUtil.decimalFormat("00000", number == null ? 1 : number + 1);
    }
}
