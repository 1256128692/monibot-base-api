package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateDataSource;
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

    //List<xxxxx>

    public static List<MonitorTypeTemplateDatasourceToken> valueOf(List<TbTemplateDataSource> list){
        if (ObjectUtil.isEmpty(list)){
            return null;
        }
       return list.stream().map(item -> MonitorTypeTemplateDatasourceToken.builder().datasourceType(item.getDataSourceType()).token(item.getTemplateDataSourceToken()).build()).collect(Collectors.toList());
    }
}
