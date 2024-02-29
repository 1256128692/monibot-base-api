package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEventType;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.TaskDataResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.TaskDateAndStatisticsInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.TaskInfo;
import cn.shmedo.monitor.monibotbaseapi.service.CheckEventService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.*;

@EnableTransactionManagement
@Service
@AllArgsConstructor
public class CheckEventServiceImpl extends ServiceImpl<TbCheckEventMapper, TbCheckEvent> implements CheckEventService {


    private final TbCheckEventTypeMapper tbCheckEventTypeMapper;

    private final TbCheckTaskMapper tbCheckTaskMapper;

    private final TbSensorMapper tbSensorMapper;

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
        return result;
    }


}
