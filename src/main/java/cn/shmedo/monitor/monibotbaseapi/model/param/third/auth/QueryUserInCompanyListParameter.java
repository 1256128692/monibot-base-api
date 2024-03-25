package cn.shmedo.monitor.monibotbaseapi.model.param.third.auth;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.third.TbService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * Created on 2021/7/16 14:19
 *
 * @Author Liuwei
 */
@Data
public class QueryUserInCompanyListParameter implements ParameterValidator {
    @Size(max = 100)
    private String companyName;
    private Boolean includeChild;
    private String accessService;

    @JsonIgnore
    private TbService service;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }
}
