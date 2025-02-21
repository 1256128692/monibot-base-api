package cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup;

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
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.redis.core.RedisTemplate;

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
public class UpdatePropertyModelGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "ID不能为空")
    @JsonProperty("ID")
    private Integer ID;

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private Integer platform;

    private Integer groupType;

    @Min(0)
    @Max(Byte.MAX_VALUE)
    private Integer groupTypeSubType;

    private String name;

    private String desc;

    private String exValue;

    @JsonIgnore
    private TbPropertyModelGroup tbPropertyModelGroup;

    @JsonIgnore
    RedisTemplate<String, String> redisTemplate = ContextHolder.getBean(RedisConstant.AUTH_REDIS_TEMPLATE);

    @Override
    public ResultWrapper<?> validate() {
        if (Objects.nonNull(groupType) &&
                !PropertyModelType.WORK_FLOW.getCode().equals(groupType) &&
                !PropertyModelType.DEVICE.getCode().equals(groupType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板组类型只支持设备和工作流");
        }
        if (Objects.nonNull(platform) && !redisTemplate.opsForHash().hasKey(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, platform.toString())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "所属平台不合法");
        }

        TbPropertyModelGroupMapper tbPropertyModelGroupMapper = ContextHolder.getBean(TbPropertyModelGroupMapper.class);
        // 校验是否存在属性模板组
        tbPropertyModelGroup = tbPropertyModelGroupMapper.selectById(this.ID);
        if (Objects.isNull(tbPropertyModelGroup)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未查询到对应模板组");
        }

        // 校验名称是否重复
        TbPropertyModelGroup tbPropertyModelGroup1 = tbPropertyModelGroupMapper.selectOne(new QueryWrapper<TbPropertyModelGroup>().lambda()
                .ne(TbPropertyModelGroup::getID, this.ID)
                .eq(TbPropertyModelGroup::getCompanyID, this.companyID)
                .eq(Objects.nonNull(groupType), TbPropertyModelGroup::getGroupType, groupType)
                .eq(TbPropertyModelGroup::getName, this.name));
        if (Objects.nonNull(tbPropertyModelGroup1)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板组名称不能重复");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
