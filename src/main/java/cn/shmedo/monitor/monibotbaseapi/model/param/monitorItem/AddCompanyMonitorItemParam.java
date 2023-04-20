package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-10 17:24
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCompanyMonitorItemParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Valid
    private List< @NotNull  Integer > monitorItemIDList;
    @Override
    public ResultWrapper validate() {

        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        List<TbMonitorItem> tbMonitorItemList = tbMonitorItemMapper.selectList(
                new QueryWrapper<TbMonitorItem>().in("ID", monitorItemIDList)
        );
        if (tbMonitorItemMapper.selectCount(
                new QueryWrapper<TbMonitorItem>().lambda()
                        .eq(TbMonitorItem::getCompanyID, companyID)
                        .eq(TbMonitorItem::getProjectID,-1)
                        .in(TbMonitorItem::getName, tbMonitorItemList.stream().map(TbMonitorItem::getName).toArray())
        ) > 0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "一些监测项目已经设置成企业模板");
        }
        if (CollectionUtils.isEmpty(tbMonitorItemList) || tbMonitorItemList.size()!= monitorItemIDList.size()){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测项目不存在");

        }
        if (tbMonitorItemList.stream().anyMatch(item -> !item.getCompanyID().equals(companyID))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测项目不属于该公司");
        }
        if (tbMonitorItemList.stream().anyMatch(item -> item.getCompanyID().equals(-1))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "预定义的监测项目不可进行设置");
        }
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
