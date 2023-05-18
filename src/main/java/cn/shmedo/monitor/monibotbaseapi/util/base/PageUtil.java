package cn.shmedo.monitor.monibotbaseapi.util.base;


import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.exception.InvalidParameterException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Liudongdong on 18/1/19.
 */
public class PageUtil {

    /**
     * 逻辑分页
     *
     * @param data        数据
     * @param pageSize    页大小
     * @param currentPage 当前页
     * @param <T>         数据类型
     * @return 分页结果
     */
    public static <T> Page<T> page(Collection<T> data, long pageSize, long currentPage) {
        if (currentPage < 1) {
            throw new InvalidParameterException("当前页不能小于1");
        }
        if (pageSize < 1) {
            throw new InvalidParameterException("页大小不能小于1");
        }
        if (CollUtil.isEmpty(data)) {
            return Page.empty();
        }
        long totalPage = (data.size() - 1) / pageSize + 1;
        long beginIndex = (currentPage - 1) * pageSize;
        if (beginIndex >= data.size())
            throw new InvalidParameterException("当前页超出总页数");
        long endIndex = currentPage * pageSize > data.size() ?
                data.size() : (currentPage * pageSize);
        return new Page<>(totalPage, data.stream().skip(beginIndex)
                .limit(endIndex - beginIndex).toList(), data.size());
    }

    public static int totalPage(int totalCount, int pageSize) {
        if (totalCount <= 0)
            return 0;
        if (pageSize < 1)
            throw new InvalidParameterException("非法的页大小参数：" + Integer.toString(pageSize));
        return (totalCount - 1) / pageSize + 1;
    }

    public record Page<T>(long totalPage, Collection<T> currentPageData, long totalCount) {

        public static <E> Page<E> empty() {
            return new Page<>(0, Collections.emptyList(), 0);
        }
    }

    public record PageWithMap<T>(long totalPage, Collection<T> currentPageData, long totalCount,
                                 Map<String, Object> map) {
        public static <E> PageWithMap<E> empty() {
            return new PageWithMap<>(0, Collections.emptyList(), 0, null);
        }

        public static <E> PageWithMap<E> emptyWithMap(Map<String, Object> map) {
            return new PageWithMap<>(0, Collections.emptyList(), 0, map);
        }
    }
}
