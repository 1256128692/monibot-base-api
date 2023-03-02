package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryProjectListRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;

    private String projectName;

    private String directManageUnit;

    private String location;

    private Integer companyId;

    private Integer projectType;

    private Boolean enable;

    private List<Integer> platformTypeList;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private DateTime expiryDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private DateTime beginCreateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private DateTime endCreateTime;


    private List<Property> propertyEntity;

    @Data
    public static class Property {

        private String name;

        private String value;

        private List<String> values;

        public void setValue(String value) {
            this.value = value;
            if (JSONUtil.isTypeJSONArray(value)) {
                this.values = JSONUtil.toList(JSONUtil.parseArray(value), String.class);
            }else {
                this.values = List.of(value);
            }
        }

        public Property() {
            this.values = new ArrayList<>();
        }
    }

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
