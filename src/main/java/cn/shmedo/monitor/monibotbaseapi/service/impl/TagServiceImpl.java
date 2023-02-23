package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTagMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.tag.QueryTagListParam;
import cn.shmedo.monitor.monibotbaseapi.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:37
 **/
@Service
public class TagServiceImpl implements TagService {
    private TbTagMapper tbTagMapper;
    @Autowired
    public TagServiceImpl(TbTagMapper tbTagMapper) {
        this.tbTagMapper = tbTagMapper;
    }

    @Override
    public List<TbTag> queryTagList(QueryTagListParam param) {
        return tbTagMapper.queryListBy(param.getCompanyID(), param.getFuzzyKey(), param.getFuzzyValue());
    }
}
