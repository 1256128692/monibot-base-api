package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.YsPageInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class YsResultPageWrapper<T> {

    private String code;
    private String msg;
    private List<T> data;

    private YsPageInfo page;

    /**
     * 接口调用是否成功
     *
     * @return
     */
    @JsonIgnore
    public boolean callSuccess() {
        return StrUtil.isNotBlank(this.code) && YsCode.SUCCESS.equals(this.code);
    }

    public static <T> YsResultPageWrapper<T> withCode(String resultCode, String msg) {
        return new YsResultPageWrapper<>(resultCode, msg, null , null);
    }
}
