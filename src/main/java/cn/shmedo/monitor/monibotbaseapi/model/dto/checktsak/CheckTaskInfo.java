package cn.shmedo.monitor.monibotbaseapi.model.dto.checktsak;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import lombok.*;

import java.util.List;

/**
 * @author Chengfs on 2024/3/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckTaskInfo extends TbCheckTask {

    private String projectName;
    private String checkerName;
    private String createUserName;
    private String updateUserName;
    private List<Note> notes;
    private List<Event> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Note {
        private Integer id;
        private String name;
        private List<Annexe> annexes;
        private String remark;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Event {
        private Integer id;
        private Integer typeID;
        private String typeName;
        private String address;
        private String location;
        private String describe;
        private List<Annexe> annexes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Annexe {
        private String name;
        private String url;
        private String fileType;
    }

    public static CheckTaskInfo valueOf(TbCheckTask task) {
        CheckTaskInfo info = new CheckTaskInfo();
        info.setID(task.getID());
        info.setProjectID(task.getProjectID());
        info.setSerialNumber(task.getSerialNumber());
        info.setCheckType(task.getCheckType());
        info.setName(task.getName());
        info.setStatus(task.getStatus());
        info.setTaskDate(task.getTaskDate());
        info.setEvaluate(task.getEvaluate());
        info.setBeginTime(task.getBeginTime());
        info.setEndTime(task.getEndTime());
        info.setCheckerID(task.getCheckerID());
        info.setTrajectory(task.getTrajectory());
        info.setExValue(task.getExValue());
        info.setCreateUserID(task.getCreateUserID());
        info.setCreateTime(task.getCreateTime());
        info.setUpdateUserID(task.getUpdateUserID());
        info.setUpdateTime(task.getUpdateTime());
        return info;
    }
}