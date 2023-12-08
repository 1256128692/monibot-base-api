package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuerySluicePageRequest extends BaseSluiceQuery {

    private String sluiceType;

    private String manageUnit;
}