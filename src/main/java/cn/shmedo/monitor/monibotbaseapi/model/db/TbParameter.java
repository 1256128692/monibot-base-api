package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 参数表
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TbParameter {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer ID;

    /**
     * 主体ID
     */
    private Integer subjectID;

    /**
     * 主体类型 1 - 公式
     * 2 - 脚本
     * 3 - 传感器
     * 4 - 模板
     */
    private Integer subjectType;

    /**
     * 数据类型，String,Double,Long
     */
    private String dataType;

    /**
     * 参数标识
     */
    private String token;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数默认值
     */
    private String paValue;

    /**
     * 计量单位ID
     */
    private Integer paUnitID;

    /**
     * 参数描述
     */
    private String paDesc;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(Integer subjectID) {
        this.subjectID = subjectID;
    }

    public Integer getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(Integer subjectType) {
        this.subjectType = subjectType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaValue() {
        return paValue;
    }

    public void setPaValue(String paValue) {
        this.paValue = paValue;
    }

    public Integer getPaUnitID() {
        return paUnitID;
    }

    public void setPaUnitID(Integer paUnitID) {
        this.paUnitID = paUnitID;
    }

    public String getPaDesc() {
        return paDesc;
    }

    public void setPaDesc(String paDesc) {
        this.paDesc = paDesc;
    }
}