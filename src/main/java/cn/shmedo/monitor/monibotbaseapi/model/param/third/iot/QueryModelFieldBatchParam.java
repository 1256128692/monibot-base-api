package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-04 17:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryModelFieldBatchParam {
    private Integer companyID;

    private List< String> modelTokenList;
}
