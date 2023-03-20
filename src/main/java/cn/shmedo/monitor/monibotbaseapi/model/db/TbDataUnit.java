package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 属性单位表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbDataUnit implements Serializable {
    private Integer ID;

    /**
    * 单位英文名称
    */
    private String engUnit;

    /**
    * 单位中文名称
    */
    private String chnUnit;

    /**
    * 单位类别
    */
    private String unitClass;

    /**
    * 单位描述信息
    */
    private String unitDesc;

    private static final long serialVersionUID = 1L;
}