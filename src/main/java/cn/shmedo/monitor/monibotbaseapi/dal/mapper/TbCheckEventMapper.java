package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.QueryEventInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.QueryEventInfoV1;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCheckEventMapper extends BasicMapper<TbCheckEvent> {
    void insertSelectColumn(TbCheckEvent toTbVo);

    void updateSelectColumn(TbCheckEvent toTbVo);

    IPage<QueryEventInfoV1> selectEventInfoPage(Page<QueryEventInfoParam> page, QueryEventInfoParam pa);
}