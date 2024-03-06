package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEventType;
import cn.shmedo.monitor.monibotbaseapi.model.dto.UserBase;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.checktask.QueryCheckTaskListRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserBatchRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.*;
import cn.shmedo.monitor.monibotbaseapi.service.CheckEventService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.util.TransferUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.*;
import java.util.stream.Collectors;

@EnableTransactionManagement
@Service
@AllArgsConstructor
public class CheckEventServiceImpl extends ServiceImpl<TbCheckEventMapper, TbCheckEvent> implements CheckEventService {


    private final TbCheckEventTypeMapper tbCheckEventTypeMapper;

    private final TbCheckTaskMapper tbCheckTaskMapper;

    private final TbSensorMapper tbSensorMapper;

    private final FileService fileService;
    private final UserService userService;
    private final FileConfig fileConfig;

    @Override
    public void addEventType(AddEventTypeParam pa) {
        tbCheckEventTypeMapper.insert(pa.toRawVo());
    }

    @Override
    public void deleteEventType(DeleteEventTypeParam pa) {
        tbCheckEventTypeMapper.deleteBatchIds(pa.getIdList());
    }

    @Override
    public void updateEventType(UpdateEventTypeParam pa) {
        tbCheckEventTypeMapper.updateSelectFieldById(pa);
    }

    @Override
    public Object queryEventTypeList(QueryEventTypeParam pa) {
        QueryWrapper<TbCheckEventType> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isNullOrEmpty(pa.getName())) {
            queryWrapper.eq("name", pa.getName());
        }
        if (ObjectUtil.isNotNull(pa.getServiceID())) {
            queryWrapper.eq("serviceID", pa.getServiceID());
        }
        return tbCheckEventTypeMapper.selectList(queryWrapper);
    }

    @Override
    public TaskDateAndStatisticsInfo queryDailyTaskList(QueryDailyTaskParam pa, Integer subjectID) {
        List<TaskInfo> taskInfoList;
        if (pa.getQueryType() == 0) {
            taskInfoList = tbCheckTaskMapper.selectListByCondition(pa, null);
        } else {
            taskInfoList = tbCheckTaskMapper.selectListByCondition(pa, subjectID);
        }
        TaskDateAndStatisticsInfo.TaskStatusInfo taskStatusInfo = calculateTaskStatusInfo(taskInfoList);
        List<TaskDataResponse> taskDataResponses = groupTaskInfoByDate(taskInfoList);
        return new TaskDateAndStatisticsInfo(taskDataResponses, taskStatusInfo);
    }

    @Override
    public void addEventInfo(AddEventInfoParam pa) {
        this.baseMapper.insertSelectColumn(pa.toTbVo());
    }

    @Override
    public void deleteEventInfo(DeleteEventInfoParam pa) {
        this.baseMapper.deleteBatchIds(pa.getEventIDList());
    }

    @Override
    public void updateEventInfo(UpdateEventInfoParam pa) {
        this.baseMapper.updateSelectColumn(pa.toTbVo());
    }

    @Override
    public Object queryEventInfoPage(QueryEventInfoParam pa) {

        transformKeyword(pa);
        IPage<QueryEventInfoV1> page = this.baseMapper.selectEventInfoPage(new Page<QueryEventInfoParam>(
                pa.getCurrentPage(), pa.getPageSize()), pa);
        List<QueryEventInfoV1> dataList = page.getRecords();

        TransferUtil.INSTANCE.getUserNameDict(dataList.stream().map(QueryEventInfoV1::getReportUserID).collect(Collectors.toList()))
                .ifPresent(dict -> {
                    for (QueryEventInfoV1 d : dataList) {
                        d.setReportUserName(dict.get(d.getReportUserID()));
                    }
                });
        return new PageUtil.Page<>(page.getPages(), dataList, page.getTotal());
    }

    /**
     * 将 {@link QueryCheckTaskListRequest} 中 keyword 转换为 用户id 用于搜索
     */
    protected void transformKeyword(QueryEventInfoParam request) {
        Optional.ofNullable(request.getQueryContent()).ifPresent(k -> {
            ResultWrapper<List<UserBase>> wrapper = userService.queryUserBatch(QueryUserBatchRequest.builder()
                            .companyID(request.getCompanyID()).name(request.getQueryContent()).build(),
                    fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
            request.setReportUserIDs(Optional.ofNullable(wrapper.getData()).orElse(List.of()).stream()
                    .map(UserBase::getUserID).distinct().toList());
        });
    }

    @Override
    public Object queryEventInfoDetail(QueryEventInfoDetailParam pa) {

        QueryEventInfoV2 eventInfoV2 = this.baseMapper.selectDetailInfoByID(pa.getEventID());
        if (!StringUtil.isNullOrEmpty(eventInfoV2.getAnnexes())) {
            List<FileInfoResponse> fileUrlList = fileService.getFileUrlList(JSONUtil.toList(eventInfoV2.getAnnexes(), String.class), pa.getCompanyID());
            eventInfoV2.setFileInfoList(fileUrlList);
        }

        TransferUtil.INSTANCE.getUserNameDict(List.of(eventInfoV2.getCheckerID(), eventInfoV2.getReportUserID()))
                .ifPresent(dict -> {
                    eventInfoV2.setCheckerName(dict.get(eventInfoV2.getCheckerID()));
                    eventInfoV2.setReportUserName(dict.get(eventInfoV2.getReportUserID()));
                });
        return eventInfoV2;
    }

    /**
     * 统计任务状态数量
     * @param taskInfoList
     * @return
     */
    private TaskDateAndStatisticsInfo.TaskStatusInfo calculateTaskStatusInfo(List<TaskInfo> taskInfoList) {
        TaskDateAndStatisticsInfo.TaskStatusInfo taskStatusInfo = new TaskDateAndStatisticsInfo.TaskStatusInfo();
        // 初始化计数器为0
        taskStatusInfo.setUnpreparedCount(0);
        taskStatusInfo.setUnderwayCount(0);
        taskStatusInfo.setExpiredCount(0);
        taskStatusInfo.setFinishedCount(0);

        for (TaskInfo taskInfo : taskInfoList) {
            int status = taskInfo.getStatus();
            switch (status) {
                case 0:
                    taskStatusInfo.setUnpreparedCount(taskStatusInfo.getUnpreparedCount() + 1);
                    break;
                case 1:
                    taskStatusInfo.setUnderwayCount(taskStatusInfo.getUnderwayCount() + 1);
                    break;
                case 2:
                    taskStatusInfo.setExpiredCount(taskStatusInfo.getExpiredCount() + 1);
                    break;
                case 3:
                    taskStatusInfo.setFinishedCount(taskStatusInfo.getFinishedCount() + 1);
                    break;
                default:
                    // 如果状态不在预期范围内，忽略
                    break;
            }
        }
        return taskStatusInfo;
    }

    /**
     * 按日期分组
     * @param taskInfoList
     * @return
     */
    private List<TaskDataResponse> groupTaskInfoByDate(List<TaskInfo> taskInfoList) {
        Map<Date, List<TaskInfo>> groupedMap = new HashMap<>();
        for (TaskInfo taskInfo : taskInfoList) {
            Date taskDate = taskInfo.getTaskDate();
            groupedMap.computeIfAbsent(taskDate, k -> new ArrayList<>()).add(taskInfo);
        }
        List<TaskDataResponse> result = new ArrayList<>();
        for (Map.Entry<Date, List<TaskInfo>> entry : groupedMap.entrySet()) {
            TaskDataResponse taskDataResponse = new TaskDataResponse(entry.getKey(), entry.getValue());
            result.add(taskDataResponse);
        }
        result.sort(Comparator.comparing(TaskDataResponse::getTaskDate));
        return result;
    }


}
