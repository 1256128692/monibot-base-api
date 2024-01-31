package cn.shmedo.monitor.monibotbaseapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2024/1/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContact {

    private Integer id;
    private String cellphone;
    private String email;
}