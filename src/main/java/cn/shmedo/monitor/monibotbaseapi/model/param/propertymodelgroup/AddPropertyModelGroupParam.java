package cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModelGroup;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;

/**
 * @Author wuxl
 * @Date 2023/9/15 17:14
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup
 * @ClassName: AddPropertyModelGroup
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class AddPropertyModelGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private Integer platform;

    @NotNull(message = "模板组类型不能为空")
    private Integer groupType;

    @Min(0)
    @Max(Byte.MAX_VALUE)
    private Integer groupTypeSubType;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String desc;

    private String exValue;

    @JsonIgnore
    RedisTemplate<String, String> redisTemplate = ContextHolder.getBean(RedisConstant.AUTH_REDIS_TEMPLATE);

    @Override
    public ResultWrapper<?> validate() {
        if(!PropertyModelType.WORK_FLOW.getCode().equals(groupType) && !PropertyModelType.DEVICE.getCode().equals(groupType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板组类型只支持设备和工作流");
        }
        if(PropertyModelType.WORK_FLOW.getCode().equals(groupType) && Objects.isNull(platform)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板组类型为工作流时，所属平台不能为空");
        }
        if(Objects.nonNull(platform) && !redisTemplate.opsForHash().hasKey(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, platform.toString())){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "所属平台不合法");
        }

        TbPropertyModelGroupMapper tbPropertyModelGroupMapper = ContextHolder.getBean(TbPropertyModelGroupMapper.class);
        // 校验名称是否重复
        List<TbPropertyModelGroup> tbPropertyModelGroupList = tbPropertyModelGroupMapper.selectList(new QueryWrapper<TbPropertyModelGroup>().lambda()
                .eq(TbPropertyModelGroup::getCompanyID, this.companyID)
                .eq(Objects.nonNull(this.platform), TbPropertyModelGroup::getPlatform, this.platform)
                .eq(TbPropertyModelGroup::getGroupType, this.groupType)
                .eq(TbPropertyModelGroup::getName, this.name));
        if (CollectionUtil.isNotEmpty(tbPropertyModelGroupList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板组名称不能重复");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
