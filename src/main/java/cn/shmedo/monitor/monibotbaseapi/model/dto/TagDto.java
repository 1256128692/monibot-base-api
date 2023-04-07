package cn.shmedo.monitor.monibotbaseapi.model.dto;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2023/3/2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDto extends TbTag {

    private Integer projectID;
}

    
    