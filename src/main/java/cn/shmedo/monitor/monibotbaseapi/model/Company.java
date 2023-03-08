package cn.shmedo.monitor.monibotbaseapi.model;

import cn.hutool.json.JSONUtil;
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

    private Integer id;
    private String shortName;
    private String fullName;
    private Integer parentID;
    private String desc;
    private String address;
    private String phone;
    private String legalPerson;
    private String scale;
    private String industry;
    private String nature;
    private String webSite;
    private String displayOrder;

}
