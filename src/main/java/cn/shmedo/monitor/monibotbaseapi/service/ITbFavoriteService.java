package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbFavorite;
import cn.shmedo.monitor.monibotbaseapi.model.param.favorite.AddFavoriteParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.favorite.DeleteFavoriteParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbFavoriteService extends IService<TbFavorite> {

    void addFavorite(AddFavoriteParam pa);

    void deleteFavorite(DeleteFavoriteParam pa);
}
