package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checkevent.CheckEventSimple;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.QueryEventInfoV2;
import jakarta.annotation.Nonnull;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.QueryEventInfoParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.QueryEventInfoV1;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbCheckEventMapper extends BasicMapper<TbCheckEvent> {
    void insertSelectColumn(TbCheckEvent toTbVo);

    void updateSelectColumn(TbCheckEvent toTbVo);

    IPage<QueryEventInfoV1> selectEventInfoPage(Page<QueryEventInfoParam> page, QueryEventInfoParam pa);

    List<CheckEventSimple> listByTaskID(@Nonnull Integer taskID);

    QueryEventInfoV2 selectDetailInfoByID(Integer eventID);

    void logicDeleteBatchIds(List<Integer> eventIDList);
}