package cn.shmedo.monitor.monibotbaseapi.util;

/**
 * @Author wuxl
 * @Date 2023/7/13 16:51
 * @PackageName:com.medo.web.util
 * @ClassName: BeanUtil
 * @Description: TODO
 * @Version 1.0
 */

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Bean工具类
 *
 * @author 22386
 */
public class CustomizeBeanUtil extends BeanUtil {
    /**
     * 集合数据的拷贝
     *
     * @param sources: 数据源类
     * @param target:  目标类::new(eg: UserVO::new)
     * @return
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, null);
    }

    public static <S, T> List<T> copyListPropertiesWithCallBack(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, new DefaultCallBack<>());
    }

    /**
     * 带回调函数的集合数据的拷贝（可自定义字段拷贝规则）
     *
     * @param sources:  数据源类
     * @param target:   目标类::new(eg: UserVO::new)
     * @param callBack: 回调函数
     * @return
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target, BeanCopyUtilCallBack<S, T> callBack) {
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source, t, true);
            if (callBack != null) {
                // 回调
                callBack.callBack(source, t);
            }
            list.add(t);
        }
        return list;
    }

    @FunctionalInterface
    public interface BeanCopyUtilCallBack<S, T> {
        /**
         * 定义默认回调方法
         *
         * @param s
         * @param t
         */
        void callBack(S s, T t);
    }

    /**
     * 默认回调函数，处理Integer和Boolean转换
     *
     * @param <S>
     * @param <T>
     */
    public static class DefaultCallBack<S, T> implements BeanCopyUtilCallBack<S, T> {
        @Override
        public void callBack(S s, T t) {
            Class<?> tClass = t.getClass();
            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                // Integer转Boolean
                if (field.getType().equals(Boolean.class)) {
                    Method tWrite, sRead;
                    try {
                        field.setAccessible(true);
                        PropertyDescriptor tPropertyDescriptor = new PropertyDescriptor(field.getName(), t.getClass());
                        tWrite = tPropertyDescriptor.getWriteMethod();
                        PropertyDescriptor sPropertyDescriptor = new PropertyDescriptor(field.getName(), s.getClass());
                        sRead = sPropertyDescriptor.getReadMethod();
                        Object sVal = sRead.invoke(s);
                        Boolean tVal = (Integer) sVal == 1;
                        tWrite.invoke(t, tVal);
                    } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("字段类型Integer转Boolean出错", e);
                    }
                }
            }
        }
    }

    public static <S, T> List<T> toBeanList(List<S> s, Class<T> clazz) {
        List<T> resList = Lists.newArrayList();
        s.forEach(e -> {
            T ins = BeanUtil.toBean(e, clazz);
            resList.add(ins);
        });
        return resList;
    }
}
