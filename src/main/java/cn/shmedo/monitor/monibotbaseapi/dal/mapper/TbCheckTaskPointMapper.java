package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTaskPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import jakarta.annotation.Nonnull;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface TbCheckTaskPointMapper extends BasicMapper<TbCheckTaskPoint> {

    /**
     * 检查一组巡检点是否存在指定状态的任务
     *
     * @param pointIds 巡检点id集合
     * @param status   {@see CheckTaskStatus}
     * @return
     */
    boolean existStatusByPointIds(@Nonnull Collection<Integer> pointIds,
                                  @Nonnull CheckTaskStatus status);
}