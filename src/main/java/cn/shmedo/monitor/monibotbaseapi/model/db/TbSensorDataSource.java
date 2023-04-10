package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 传感器数据来源
 */
public class TbSensorDataSource {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer ID;

    /**
     * 数据源分布式唯一ID
     */
    private String dataSourceID;

    /**
     * 数据源类型 1 - 物联网传感器
     * 2 - 监测传感器
     */
    private Integer dataSourceType;

    /**
     * 数据源标识 1.物联网uniqueToken@物联网传感器名称  2.监测传感器名称
     */
    private String dataSourceToken;

    /**
     * 模板数据源标识，使用代号。
     * 在公式中可以使用代号引用对应的数据源。
     * 物联网传感器代号：203_a,203_b,999_a
     * 监测传感器代号：22_a
     */
    private String templateDataSourceToken;

    /**
     * 模板数据来源类型
     * 1 -  单一物模型单一传感器
     * 2 -  多个物联网传感器（同一物模型多个或者不同物模型多个）
     * 3 -  物联网传感器+监测传感器
     * 4 - 1个监测传感器
     * 5 - 多个监测传感器
     * 100 - API 推送    (无tb_template_data_source实体)
     * 500 - 人工监测数据  (无tb_template_data_source实体)
     */
    private Integer dataSourceComposeType;

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

    public String getDataSourceID() {
        return dataSourceID;
    }

    public void setDataSourceID(String dataSourceID) {
        this.dataSourceID = dataSourceID;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getDataSourceToken() {
        return dataSourceToken;
    }

    public void setDataSourceToken(String dataSourceToken) {
        this.dataSourceToken = dataSourceToken;
    }

    public String getTemplateDataSourceToken() {
        return templateDataSourceToken;
    }

    public void setTemplateDataSourceToken(String templateDataSourceToken) {
        this.templateDataSourceToken = templateDataSourceToken;
    }

    public Integer getDataSourceComposeType() {
        return dataSourceComposeType;
    }

    public void setDataSourceComposeType(Integer dataSourceComposeType) {
        this.dataSourceComposeType = dataSourceComposeType;
    }

    public String getExValues() {
        return exValues;
    }

    public void setExValues(String exValues) {
        this.exValues = exValues;
    }
}