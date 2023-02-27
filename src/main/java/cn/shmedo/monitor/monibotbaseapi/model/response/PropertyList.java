package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.Data;

/**
 * @Author cyf
 * @Date 2023/2/27 11:44
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response
 * @ClassName: PropertyList
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class PropertyList {
    private Integer ID;
    private Integer projectID;
    private Integer propertyID;
    private Integer type;
    private Integer projectType;
    private String className;
    private String name;
    private Boolean required;
    private Boolean multiSelect;
    private Integer createType;
    private String enumField;
    private String unit;
    private String value;
    private String exValue;
    private Integer displayOrder;
}
