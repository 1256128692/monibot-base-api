package cn.shmedo.monitor.monibotbaseapi.model.response.third.auth;

public class NotifyStatisticsInfo {

    /**
     * 通知总数
     */
    private Integer totalCount;
    /**
     * 已读通知数量
     */
    private Integer readCount;
    /**
     * 未读通知数量
     */
    private Integer unreadCount;

    /**
     * 待办通知数量
     */
    private Integer todoCount;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Integer getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(Integer todoCount) {
        this.todoCount = todoCount;
    }
}
