package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.shmedo.iot.entity.base.SubjectType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数
 *
 * @author Chengfs on 2023/3/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParameterCacheData extends AbstractCacheData {

    private Integer subjectID;

    private SubjectType subjectType;

    private String dataType;

    private String token;

    private String name;

    private String paValue;

    private Integer paUnitID;

    private String paDesc;
}

    
    