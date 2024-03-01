package cn.shmedo.monitor.monibotbaseapi.util;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-01 09:43
 */
public final class MybatisFieldUtil {
    /**
     * (non-Javadoc)
     *
     * @see com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper#columnsToString(SFunction[])
     */
    @SuppressWarnings("JavadocReference")
    public static <T> String columnToString(final SFunction<T, ?> column) {
        return Optional.ofNullable(column).map(LambdaUtils::extract).flatMap(u ->
                        Optional.ofNullable(u.getInstantiatedClass()).map(LambdaUtils::getColumnMap).flatMap(w ->
                                Optional.ofNullable(u.getImplMethodName()).map(PropertyNamer::methodToProperty)
                                        .map(LambdaUtils::formatKey).map(w::get))).map(ColumnCache::getColumn)
                .orElseThrow(() -> new RuntimeException("get entity or property failed."));
    }
}
