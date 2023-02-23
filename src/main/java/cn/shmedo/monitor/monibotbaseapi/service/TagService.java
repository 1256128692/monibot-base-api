package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.tag.QueryTagListParam;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:04
 **/
public interface TagService {
    List<TbTag> queryTagList(QueryTagListParam param);
}
