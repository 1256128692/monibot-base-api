package cn.shmedo.monitor.monibotbaseapi.util;

import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * simple wrapper.
 *
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 16:23
 */
@AllArgsConstructor
public final class CustomWrapper<T> {
    private T value;

    public void setValue(final Function<T, T> func) {
        this.value = func.apply(this.value);
    }

    public T get() {
        return this.value;
    }
}
