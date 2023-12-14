package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.util.CustomWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-12-13 17:57
 */
@Data
public class AddManualDataBatchV2Param implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空!")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotEmpty(message = "数据列表不能为空")
    private List<@Valid AddManualBatchV2Data> dataList;
    @JsonIgnore
    private final AddManualDataBatchParam param = new AddManualDataBatchParam();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ResultWrapper validate() {
        // check field list
        CustomWrapper<ResultWrapper<?>> wrapper = new CustomWrapper<>(null);
        dataList.stream().map(AddManualBatchV2Data::getFieldList).flatMap(Collection::stream).peek(u -> {
            if (Objects.isNull(wrapper.get())) {
                if (!u.containsKey("fieldToken") || ObjectUtil.isEmpty(u.get("fieldToken"))) {
                    wrapper.setValue(v -> ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性标识不能为空"));
                } else if (!u.containsKey("value") || ObjectUtil.isEmpty(u.get("value"))) {
                    wrapper.setValue(v -> ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性值不能为空"));
                }
            }
        }).toList();
        ResultWrapper<?> resultWrapper = wrapper.get();
        if (Objects.nonNull(resultWrapper)) {
            return resultWrapper;
        }
        param.setProjectID(projectID);
        param.setDataList(dataList.stream().map(u -> {
            Integer sensorID = u.getSensorID();
            Date time = u.getTime();
            return u.getFieldList().stream().map(w -> {
                ManualDataItem item = new ManualDataItem();
                item.setSensorID(sensorID);
                item.setTime(time);
                item.setFieldToken(w.get("fieldToken"));
                item.setValue(w.get("value"));
                return item;
            }).toList();
        }).flatMap(Collection::stream).toList());
        // check others
        ResultWrapper<?> validate = param.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
