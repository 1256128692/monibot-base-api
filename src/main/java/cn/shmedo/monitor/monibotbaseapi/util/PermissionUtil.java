package cn.shmedo.monitor.monibotbaseapi.util;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;

public class PermissionUtil {
    /**
     * 将"serviceName:permissionToken"拆分为具体的服务和权限
     *
     * @param permissionName
     * @return
     */
    public static Tuple<String, String> splitServiceAndPermission(String permissionName) {
        String[] parts = permissionName.split(DefaultConstant.COLON);
        return new Tuple<>(parts[0], parts[1]);
    }
}
