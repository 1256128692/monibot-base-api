package cn.shmedo.monitor.monibotbaseapi.model.param.checkevent;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeleteEventTypeParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @NotEmpty
    private List<@NotNull Integer> idList;



    @Override
    public ResultWrapper validate() {
        TbCheckEventMapper checkEventMapper = ContextHolder.getBean(TbCheckEventMapper.class);
        List<TbCheckEvent> checkEvents = checkEventMapper.selectList(new QueryWrapper<TbCheckEvent>().in("TypeID", idList));

        if (!CollectionUtil.isNullOrEmpty(checkEvents)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "所选事件类型有绑定的事件,无法删除");
        }

        return null;
    }

    @Override
    public Resource parameter() {

        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

}
