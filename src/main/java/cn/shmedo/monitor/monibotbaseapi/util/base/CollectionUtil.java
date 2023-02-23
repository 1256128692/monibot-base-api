package cn.shmedo.monitor.monibotbaseapi.util.base;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created  on 2015/10/7.
 *
 * @author Liudongdong
 */
public class CollectionUtil {
    public static boolean isNullOrEmpty(List data) {
        return data == null || data.size() == 0;
    }

    /**
     * 把一个大的List成多个小的List
     *
     * @param data
     * @param seperatorSize
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> seperatorList(List<T> data, int seperatorSize) {
        List<T> list = Collections.synchronizedList(data);

        if (CollectionUtil.isNullOrEmpty(data)) {
            return Collections.emptyList();
        }
        List<List<T>> result = new LinkedList<>();

        while (data.size() > seperatorSize) {
            List<T> pre = data.subList(0, seperatorSize);
            data = data.subList(seperatorSize, data.size());
            result.add(pre);
        }
        if (!CollectionUtil.isNullOrEmpty(data)) {
            result.add(data);
        }
        return result;
    }
}
