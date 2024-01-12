package cn.shmedo.monitor.monibotbaseapi.model.param.region;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListAreaCodeByLocationNameParam implements ParameterValidator {
    @NotEmpty(message = "地区名称列表不能为空")
    @Size(max = 50)
    private List<String> locationNameList;

    @Override
    public ResultWrapper validate() {
        locationNameList = locationNameList.stream().distinct().collect(Collectors.toList());
        return null;
    }
}
