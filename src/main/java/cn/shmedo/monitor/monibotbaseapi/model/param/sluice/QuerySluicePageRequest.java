package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuerySluicePageRequest extends BaseSluiceQuery {

    private String sluiceType;

    private String manageUnit;

    @JsonIgnore
    private Set<Integer> sensorList = new HashSet<>();
}