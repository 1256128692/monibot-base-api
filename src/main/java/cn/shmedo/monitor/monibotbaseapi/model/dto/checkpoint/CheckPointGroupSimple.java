package cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2024/2/29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckPointGroupSimple {

    private Integer id;
    private Integer projectID;
    private String name;
    private String exValue;

    public static CheckPointGroupSimple valueOf(TbCheckPointGroup entity) {
        return CheckPointGroupSimple.builder()
                .id(entity.getID())
                .projectID(entity.getProjectID())
                .name(entity.getName())
                .exValue(entity.getExValue())
                .build();
    }
}