package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateFormula;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-29 10:03
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class TbMonitorTypeTemplate4Web extends TbMonitorTypeTemplate {
    private List<MonitorTypeTemplateDatasourceToken> tokenList;
    private String script;
    private List<TbTemplateFormula> formulaList;

}
