package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-26 09:57
 */
@Getter
public class WtProductVideoTypeInfo {
    private final List<WtVideoTypeInfo> videoTypeInfoList = new ArrayList<>();
    private final List<WtProductTypeInfo> productTypeInfoList = new ArrayList<>();

    public void addVideoType(WtVideoTypeInfo info) {
        this.videoTypeInfoList.add(info);
    }

    public void addProductType(WtProductTypeInfo info) {
        this.productTypeInfoList.add(info);
    }
}
