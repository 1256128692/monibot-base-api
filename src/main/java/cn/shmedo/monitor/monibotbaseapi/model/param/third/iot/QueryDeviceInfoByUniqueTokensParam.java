package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryDeviceInfoByUniqueTokensParam {

    private List<String> uniqueTokens;

}
