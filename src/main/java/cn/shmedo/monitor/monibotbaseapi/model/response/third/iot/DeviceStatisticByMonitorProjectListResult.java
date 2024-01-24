package cn.shmedo.monitor.monibotbaseapi.model.response.third.iot;

public class DeviceStatisticByMonitorProjectListResult {
    private Integer projectID;
    private long activeCount;
    private long activeSuccessCount;
    private long otaCount;
    private long otaSuccessCount;
    private long execCount;
    private long execSuccessCount;
    private long onlineCount;
    private long offlineCount;
    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Long activeCount) {
        this.activeCount = activeCount;
    }

    public Long getActiveSuccessCount() {
        return activeSuccessCount;
    }

    public void setActiveSuccessCount(Long activeSuccessCount) {
        this.activeSuccessCount = activeSuccessCount;
    }

    public Long getOtaCount() {
        return otaCount;
    }

    public void setOtaCount(Long otaCount) {
        this.otaCount = otaCount;
    }

    public Long getOtaSuccessCount() {
        return otaSuccessCount;
    }

    public void setOtaSuccessCount(Long otaSuccessCount) {
        this.otaSuccessCount = otaSuccessCount;
    }

    public Long getExecCount() {
        return execCount;
    }

    public void setExecCount(Long execCount) {
        this.execCount = execCount;
    }

    public Long getExecSuccessCount() {
        return execSuccessCount;
    }

    public void setExecSuccessCount(Long execSuccessCount) {
        this.execSuccessCount = execSuccessCount;
    }

    public Long getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(Long onlineCount) {
        this.onlineCount = onlineCount;
    }

    public Long getOfflineCount() {
        return offlineCount;
    }

    public void setOfflineCount(Long offlineCount) {
        this.offlineCount = offlineCount;
    }
}
