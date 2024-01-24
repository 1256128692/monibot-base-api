package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author wuxl
 * @Date 2024/1/23 17:08
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.dashboard
 * @ClassName: QueryProvinceProjectParam
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class QueryProvinceProjectParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    /**
     * 项目一级分类
     */
    private Byte projectMainType;


    @Override
    public ResultWrapper validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }
}
