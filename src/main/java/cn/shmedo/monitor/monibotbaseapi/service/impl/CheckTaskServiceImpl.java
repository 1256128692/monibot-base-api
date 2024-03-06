package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.UserBase;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checktsak.CheckTaskInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checktsak.CheckTaskSimple;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import cn.shmedo.monitor.monibotbaseapi.model.param.checktask.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserBatchRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.checktask.CheckTaskPageResponse;
import cn.shmedo.monitor.monibotbaseapi.service.CheckTaskService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.TransferUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.XJ;

@Service
@RequiredArgsConstructor
public class CheckTaskServiceImpl extends ServiceImpl<TbCheckTaskMapper, TbCheckTask> implements CheckTaskService {

    private final TbCheckTaskPointMapper taskPointMapper;
    private final TbCheckEventMapper eventMapper;
    private final TbProjectInfoMapper projectInfoMapper;
    private final TbCheckPointMapper pointMapper;
    private final UserService userService;
    private final FileService fileService;
    private final FileConfig fileConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(AddCheckTaskRequest request) {
        TbCheckTask entity = request.toEntity();
        entity.setSerialNumber(generateSerialNumber());
        this.save(entity);

        List<TbCheckTaskPoint> points = request.toEntities(entity.getID());
        taskPointMapper.batchInsert(points);
        return entity.getID();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateCheckTaskRequest request) {
        TbCheckTask entity = request.toEntity();
        this.updateById(entity);

        List<TbCheckTaskPoint> points = request.toEntities(entity.getID());
        taskPointMapper.delete(Wrappers.<TbCheckTaskPoint>lambdaQuery()
                .eq(TbCheckTaskPoint::getTaskID, entity.getID()));
        taskPointMapper.batchInsert(points);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeleteCheckTaskRequest request) {
        taskPointMapper.delete(Wrappers.<TbCheckTaskPoint>lambdaQuery()
                .in(TbCheckTaskPoint::getTaskID, request.getIdList()));
        eventMapper.delete(Wrappers.<TbCheckEvent>lambdaQuery()
                .in(TbCheckEvent::getTaskID, request.getIdList()));
        this.removeByIds(request.getIdList());
    }

    @Override
    public CheckTaskPageResponse page(QueryCheckTaskPageRequest request) {
        transformKeyword(request);
        Page<CheckTaskSimple> page = baseMapper.page(request, new Page<>(request.getCurrentPage(), request.getPageSize()));

        CheckTaskPageResponse.Statis stats = new CheckTaskPageResponse.Statis();
        Map<Integer, Long> statsMap = baseMapper.statis(request).stream()
                .collect(Collectors.toMap(Tuple::getItem1, Tuple::getItem2));
        statsMap.forEach((k, v) -> {
            switch (k) {
                case 0 -> stats.setNotStartCount(v);
                case 1 -> stats.setOngoingCount(v);
                case 2 -> stats.setExpiredCount(v);
                case 3 -> stats.setEndedCount(v);
            }
        });
        stats.setTotalCount(statsMap.values().stream().mapToLong(e -> e).sum());
        TransferUtil.INSTANCE.getUserNameDict(page.getRecords().stream()
                        .map(CheckTaskSimple::getCheckerID).distinct().toList())
                .ifPresent(dict -> page.getRecords().forEach(e -> e.setCheckerName(dict.get(e.getCheckerID()))));
        return CheckTaskPageResponse.builder()
                .currentPageData(page.getRecords())
                .totalPage(page.getPages())
                .totalCount(page.getTotal())
                .statis(stats)
                .build();
    }

    @Override
    public List<CheckTaskSimple> list(QueryCheckTaskListRequest request) {
        transformKeyword(request);
        List<CheckTaskSimple> list = baseMapper.list(request);
        TransferUtil.INSTANCE.getUserNameDict(list.stream()
                        .map(CheckTaskSimple::getCheckerID).distinct().toList())
                .ifPresent(dict -> list.forEach(e -> e.setCheckerName(dict.get(e.getCheckerID()))));
        return list;
    }

    @Override
    public CheckTaskInfo single(QueryCheckTaskRequest request) {
        CheckTaskInfo data = CheckTaskInfo.valueOf(request.getEntity());

        // 处理用户信息
        TransferUtil.INSTANCE.getUserNameDict(List.of(data.getCheckerID(), data.getCreateUserID(), data.getUpdateUserID()))
                .ifPresent(dict -> {
                    data.setCheckerName(dict.get(data.getCheckerID()));
                    data.setCreateUserName(dict.get(data.getCreateUserID()));
                    data.setUpdateUserName(dict.get(data.getUpdateUserID()));
                });

        //处理项目信息
        TbProjectInfo project = projectInfoMapper.selectOne(Wrappers.<TbProjectInfo>lambdaQuery()
                .eq(TbProjectInfo::getID, data.getProjectID())
                .select(TbProjectInfo::getProjectName, TbProjectInfo::getCompanyID));
        data.setProjectName(project.getProjectName());

        List<CheckTaskInfo.Note> notes = taskPointMapper.selectList(Wrappers.<TbCheckTaskPoint>lambdaQuery()
                        .eq(TbCheckTaskPoint::getTaskID, request.getId())
                        .select(TbCheckTaskPoint::getID, TbCheckTaskPoint::getPointInfo,
                                TbCheckTaskPoint::getAnnexes, TbCheckTaskPoint::getRemark))
                .stream().map(e -> {
                    TbCheckPoint point = JSONUtil.toBean(e.getPointInfo(), TbCheckPoint.class);
                    List<CheckTaskInfo.Annexe> annexes = Optional
                            .ofNullable(JSONUtil.toList(e.getAnnexes(), String.class)).orElse(List.of())
                            .stream().map(i -> new CheckTaskInfo.Annexe(i, null, null)).toList();
                    return new CheckTaskInfo.Note(point.getID(), point.getName(), annexes, e.getRemark());
                }).toList();

        List<CheckTaskInfo.Event> events = eventMapper.listByTaskID(request.getId()).stream().map(e -> {
            List<CheckTaskInfo.Annexe> annexes = Optional
                    .ofNullable(JSONUtil.toList(e.getAnnexes(), String.class)).orElse(List.of())
                    .stream().map(i -> new CheckTaskInfo.Annexe(i, null, null)).toList();
            return new CheckTaskInfo.Event(e.getId(), e.getTypeID(), e.getTypeName(), e.getAddress(),
                    e.getLocation(), e.getDescribe(), annexes);
        }).toList();

        //附件字典
        Map<String, FileInfoResponse> fileMap = fileService.getFileUrlList(Stream.concat(notes.stream()
                                        .flatMap(e -> e.getAnnexes().stream()),
                                events.stream().flatMap(e -> e.getAnnexes().stream()))
                        .map(CheckTaskInfo.Annexe::getName).distinct().toList(), project.getCompanyID())
                .stream().collect(Collectors.toMap(FileInfoResponse::getFilePath, e -> e));

        notes.forEach(e -> e.setAnnexes(fillFileUrl(e.getAnnexes(), fileMap)));
        events.forEach(e -> e.setAnnexes(fillFileUrl(e.getAnnexes(), fileMap)));
        data.setEvents(events);
        data.setNotes(notes);

        return data;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startTask(StartTaskRequest request) {
        boolean updated = this.update(Wrappers.<TbCheckTask>lambdaUpdate()
                .eq(TbCheckTask::getID, request.getTaskID())
                .set(TbCheckTask::getStatus, CheckTaskStatus.PROCESSING)
                .set(TbCheckTask::getBeginTime, request.getStartTime())
                .set(TbCheckTask::getUpdateUserID, request.getSubject().getSubjectID()));
        Assert.isTrue(updated, "开始巡检任务失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endTask(EndTaskRequest request) {
        taskPointMapper.updateBatchSelective(request.getTaskPoints());
        pointMapper.updateLastCheckTime(request.getEndTime(), request.getNoteMap().keySet());
        boolean updated = this.update(Wrappers.<TbCheckTask>lambdaUpdate()
                .eq(TbCheckTask::getID, request.getTaskID())
                .set(TbCheckTask::getStatus, CheckTaskStatus.FINISHED)
                .set(TbCheckTask::getEvaluate, request.getEvaluate())
                .set(TbCheckTask::getTrajectory, request.getTrajectory())
                .set(TbCheckTask::getEndTime, request.getEndTime())
                .set(TbCheckTask::getUpdateUserID, request.getSubject().getSubjectID()));
        Assert.isTrue(updated, "结束巡检任务失败");
    }

    /**
     * 将 {@link QueryCheckTaskListRequest} 中 keyword 转换为 用户id 用于搜索
     */
    protected void transformKeyword(QueryCheckTaskListRequest request) {
        Optional.ofNullable(request.getKeyword()).ifPresent(k -> {
            ResultWrapper<List<UserBase>> wrapper = userService.queryUserBatch(QueryUserBatchRequest.builder()
                            .companyID(request.getCompanyID()).name(request.getKeyword()).build(),
                    fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
            request.setCheckerList(Optional.ofNullable(wrapper.getData()).orElse(List.of()).stream()
                    .map(UserBase::getUserID).distinct().toList());
        });
    }

    /**
     * 任务编号 由首字母XJ、当前时间（年月日）、5位序列号（00001开始)组成
     *
     * @return 巡检任务编号
     */
    protected synchronized String generateSerialNumber() {
        String prefix = XJ + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Integer number = baseMapper.lastSerialNumber(prefix);
        return prefix + NumberUtil.decimalFormat("00000", number == null ? 1 : number + 1);
    }

    protected List<CheckTaskInfo.Annexe> fillFileUrl(List<CheckTaskInfo.Annexe> list, Map<String, FileInfoResponse> fileMap) {
       return list.stream().peek(a -> Optional.ofNullable(fileMap.get(a.getName())).ifPresent(f -> {
            a.setName(f.getFileName());
            a.setFileType(f.getFileType());
            a.setUrl(f.getAbsolutePath());
        })).filter(a -> a.getUrl() != null).toList();
    }

}
