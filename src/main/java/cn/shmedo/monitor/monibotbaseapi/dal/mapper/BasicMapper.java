package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * {@link BaseMapper} 扩展，增加批量插入方法
 */
public interface BasicMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入 仅适用于mysql
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(List<T> entityList);

    /**
     * 根据Id部分字段更新 仅适用于mysql
     *
     * @param entity 实体
     * @return 影响行数
     */
    Integer alwaysUpdateSomeColumnById(@Param("et") T entity);
}
