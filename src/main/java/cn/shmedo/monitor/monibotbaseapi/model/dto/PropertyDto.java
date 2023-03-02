package cn.shmedo.monitor.monibotbaseapi.model.dto;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2023/3/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PropertyDto extends TbProperty {

    private Integer projectID;

    private Integer propertyID;

    private String value;
}

    
    