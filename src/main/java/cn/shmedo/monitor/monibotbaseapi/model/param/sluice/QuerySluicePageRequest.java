package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

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

    @NotNull
    @Range(min = 1, max = 100)
    private Integer pageSize;

    @NotNull
    @Positive
    private Integer currentPage;

    @JsonIgnore
    private Set<Integer> sensorList = new HashSet<>();
}