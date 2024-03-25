package cn.shmedo.monitor.monibotbaseapi.model.dto.checkevent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckEventSimple {

    private Integer id;
    private Integer typeID;
    private String typeName;
    private String address;
    private String location;
    private String describe;
    private String annexes;
    private LocalDateTime reportTime;
}