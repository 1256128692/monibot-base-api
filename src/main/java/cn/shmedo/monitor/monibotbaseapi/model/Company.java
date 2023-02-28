package cn.shmedo.monitor.monibotbaseapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公司信息表
 * @Author cyf
 * @Date 2023/2/22 11:54
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.data
 * @ClassName: Company
 * @Description:
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private Integer ID;
    private String ShortName;
    private String FullName;
    private Integer ParentID;
    private String Desc;
    private String Address;
    private String Phone;
    private String LegalPerson;
    private String Scale;
    private String Industry;
    private String Nature;
    private String WebSite;
    private String displayOrder;

}
