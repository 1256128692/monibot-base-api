package cn.shmedo.monitor.monibotbaseapi.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class AbstractCacheData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer ID;
}

    
    