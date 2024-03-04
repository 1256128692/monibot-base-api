package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTaskPoint;
import jakarta.annotation.Nonnull;
import org.apache.ibatis.annotations.Mapper;
import reactor.util.function.Tuple3;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TbCheckTaskPointMapper extends BasicMapper<TbCheckTaskPoint> {

    int updateBatchSelective(@Nonnull List<TbCheckTaskPoint> list);

    int batchInsert(@Nonnull List<TbCheckTaskPoint> list);

    /**
     * 查询巡检点及其关联任务状态
     * @param pointIds 巡检点id
     * @return t1: tb_check_task_point id; t2: 巡检点id; t3: 任务状态
     */
    List<Tuple3<Integer, Integer, Integer>> listPointStatus(@Nonnull Collection<Integer> pointIds);
}