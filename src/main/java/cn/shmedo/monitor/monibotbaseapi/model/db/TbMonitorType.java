package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
 * 监测类型
 */
public class TbMonitorType {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer ID;

    /**
     * 小于10000是预定义预留
     * <p>
     * 整个系统内不重复
     */
    private Integer monitorType;

    /**
     * 监测类型名称
     */
    private String typeName;

    /**
     * 监测类型别名
     */
    private String typeAlias;

    /**
     * 排序字段
     */
    private Integer displayOrder;

    /**
     * 监测类型的类别
     */
    private String monitorTypeClass;

    /**
     * 监测点允许关联多传感器标识
     */
    private Boolean multiSensor;

    /**
     * 允许api数据源
     */
    private Boolean apiDataSource;

    /**
     * 创建类型
     */
    private Byte createType;

    /**
     * 公司ID，预定义监测类型为-1
     */
    private Integer companyID;

    /**
     * 拓展字段
     */
    private String exValues;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeAlias() {
        return typeAlias;
    }

    public void setTypeAlias(String typeAlias) {
        this.typeAlias = typeAlias;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getMonitorTypeClass() {
        return monitorTypeClass;
    }

    public void setMonitorTypeClass(String monitorTypeClass) {
        this.monitorTypeClass = monitorTypeClass;
    }

    public Boolean getMultiSensor() {
        return multiSensor;
    }

    public void setMultiSensor(Boolean multiSensor) {
        this.multiSensor = multiSensor;
    }

    public Boolean getApiDataSource() {
        return apiDataSource;
    }

    public void setApiDataSource(Boolean apiDataSource) {
        this.apiDataSource = apiDataSource;
    }

    public Byte getCreateType() {
        return createType;
    }

    public void setCreateType(Byte createType) {
        this.createType = createType;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getExValues() {
        return exValues;
    }

    public void setExValues(String exValues) {
        this.exValues = exValues;
    }
}