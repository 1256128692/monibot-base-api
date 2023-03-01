package cn.shmedo.monitor.monibotbaseapi.model.param.region;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2023/3/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLocationParam implements ParameterValidator {

    @NotNull(message = "地区编号不能为空")
    @Min(value = 110000, message = "地区编号不能小于110000")
    private Integer code;

    @Override
    public ResultWrapper<?> validate() {
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        if (!redisService.hasKey(RedisKeys.REGION_AREA_KEY, code.toString())) {
            throw new InvalidParameterException("地区编号不存在");
        }
        return null;
    }
}

    
    