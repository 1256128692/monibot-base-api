package cn.shmedo.monitor.monibotbaseapi.model.response.third;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserIDName {
    private Integer userID;
    private String userName;
}
