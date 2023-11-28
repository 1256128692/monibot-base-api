package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * 批量指令下发 参数
 *
 * @author Chengfs on 2023/11/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryDeviceSimpleByUniqueTokensParam {

    private Collection<String> uniqueTokens;
}

    
    