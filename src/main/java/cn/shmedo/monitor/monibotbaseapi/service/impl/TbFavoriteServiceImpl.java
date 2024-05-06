package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.FavoriteSubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.favorite.AddFavoriteParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.favorite.DeleteFavoriteParam;
import cn.shmedo.monitor.monibotbaseapi.service.ITbFavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TbFavoriteServiceImpl extends ServiceImpl<TbFavoriteMapper, TbFavorite> implements ITbFavoriteService {

    @Override
    public void addFavorite(AddFavoriteParam pa) {
        List<TbFavorite> favoriteList = pa.getFavoriteList();
        this.saveBatch(favoriteList);
    }

    @Override
    public void deleteFavorite(DeleteFavoriteParam pa) {
        this.remove(new LambdaQueryWrapper<TbFavorite>()
                .eq(TbFavorite::getCompanyID, pa.getCompanyID())
                .eq(TbFavorite::getSubjectType, FavoriteSubjectType.MONITOR_PROJECT.getType())
                .in(TbFavorite::getSubjectID, pa.getIDList()));
    }
}
