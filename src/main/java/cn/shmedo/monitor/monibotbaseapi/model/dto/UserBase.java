package cn.shmedo.monitor.monibotbaseapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 用户基础信息
 *
 * @author Chengfs on 2023/5/11
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserBase {

    private Integer companyID;

    private String companyName;

    private Integer userID;

    private String account;

    private String name;

    private String cellPhone;

    public static UserBase empty() {
        return UserBase.builder().build();
    }
}