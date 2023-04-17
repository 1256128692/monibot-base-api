package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateDataSource;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.ModelField;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-29 10:46
 **/
@Data
@Builder(toBuilder = true)
public class MonitorTypeTemplateDatasourceToken {
    private Integer datasourceType;
    private String token;

    private String iotModelToken;
    private List<ModelField> iotModelFieldList;

    public static List<MonitorTypeTemplateDatasourceToken> valueOf(List<TbTemplateDataSource> list) {
        if (ObjectUtil.isEmpty(list)) {
            return null;
        }
        return list.stream().map(item -> MonitorTypeTemplateDatasourceToken.builder().
                datasourceType(item.getDataSourceType()).token(item.getTemplateDataSourceToken())
                .iotModelToken(item.getDataSourceType().equals(DataSourceType.IOT_SENSOR.getCode()) ? item.getTemplateDataSourceToken().split("_")[0] : null)
                .build()).collect(Collectors.toList());
    }
}
