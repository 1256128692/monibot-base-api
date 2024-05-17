package cn.shmedo.monitor.monibotbaseapi.model.param.favorite;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbFavorite;
import cn.shmedo.monitor.monibotbaseapi.model.enums.FavoriteSubjectType;
import cn.shmedo.monitor.monibotbaseapi.service.ITbFavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Data
public class AddFavoriteParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司id不能为空")
    private Integer companyID;
    @NotNull(message = "收藏类型不能为空")
    private Integer subjectType;
    @NotEmpty(message = "收藏id不能为空")
    private Set<Integer> subjectIDSet;

    @Override
    public ResultWrapper<?> validate() {
        ITbFavoriteService favoriteService = ContextHolder.getBean(ITbFavoriteService.class);
        List<TbFavorite> list = favoriteService.list(new LambdaQueryWrapper<TbFavorite>()
                .eq(TbFavorite::getCompanyID, companyID)
                .eq(TbFavorite::getSubjectType, subjectType)
                .in(TbFavorite::getSubjectID, subjectIDSet));
        if(CollectionUtil.isNotEmpty(list))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在重复收藏的选项");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    public List<TbFavorite> getFavoriteList() {
        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        List<TbFavorite> tbFavoriteList = new ArrayList<>(subjectIDSet.size());
        Date date = new Date();
        for (Integer id : subjectIDSet) {
            TbFavorite tbFavorite = new TbFavorite()
                    .setCompanyID(companyID)
                    .setSubjectID(id)
                    .setSubjectType(FavoriteSubjectType.MONITOR_PROJECT.getType())
                    .setCreateUserID(subjectID)
                    .setCreateTime(date);
            tbFavoriteList.add(tbFavorite);
        }
        return tbFavoriteList;
    }
}
