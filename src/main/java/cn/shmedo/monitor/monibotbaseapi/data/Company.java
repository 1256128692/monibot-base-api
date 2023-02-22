package cn.shmedo.monitor.monibotbaseapi.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公司信息表
 * @Author cyf
 * @Date 2023/2/22 11:54
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.data
 * @ClassName: Company
 * @Description: TODO
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private int companyID;
    private String companyName;
    private String companyContacts;
    private String contactsPhone;
    private String contactsAddress;

}
