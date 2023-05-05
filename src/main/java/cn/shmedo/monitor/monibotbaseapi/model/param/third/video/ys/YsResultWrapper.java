package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class YsResultWrapper<T> {
    private String code;
    private String msg;
    private T data;

    /**
     * 接口调用是否成功
     *
     * @return
     */
    @JsonIgnore
    public boolean callSuccess() {
        return StrUtil.isNotBlank(this.code) && YsCode.SUCCESS.equals(this.code);
    }

    public static <T> YsResultWrapper<T> withCode(String resultCode, String msg) {
        return new YsResultWrapper<>(resultCode, msg, null);
    }


}
