package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 海康云台操作枚举
 *
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-05 10:53
 * @see <a href="https://open.hikvision.com/docs/docId?productId=5c67f1e2f05948198c909700&version=%2F60df74fdc6f24041ac3d2d7f81c32325&tagPath=API%E5%88%97%E8%A1%A8-%E8%A7%86%E9%A2%91%E4%B8%9A%E5%8A%A1-%E8%A7%86%E9%A2%91%E5%8A%9F%E8%83%BD#e6643a97">接口文档-根据监控点编号进行云台操作</a>
 */
@Getter
@RequiredArgsConstructor
public enum HikPtzCommandEnum {
    /**
     * 左转
     */
    LEFT("LEFT"),
    /**
     * 右转
     */
    RIGHT("RIGHT"),
    /**
     * 上转
     */
    UP("UP"),
    /**
     * 下转
     */
    DOWN("DOWN"),
    /**
     * 焦距变大
     */
    ZOOM_IN("ZOOM_IN"),
    /**
     * 焦距变小
     */
    ZOOM_OUT("ZOOM_OUT"),
    /**
     * 左上
     */
    LEFT_UP("LEFT_UP"),
    /**
     * 左下
     */
    LEFT_DOWN("LEFT_DOWN"),
    /**
     * 右上
     */
    RIGHT_UP("RIGHT_UP"),
    /**
     * 右下
     */
    RIGHT_DOWN("RIGHT_DOWN"),
    /**
     * 焦点前移
     */
    FOCUS_NEAR("FOCUS_NEAR"),
    /**
     * 焦点后移
     */
    FOCUS_FAR("FOCUS_FAR"),
    /**
     * 光圈扩大
     */
    IRIS_ENLARGE("IRIS_ENLARGE"),
    /**
     * 光圈缩小
     */
    IRIS_REDUCE("IRIS_REDUCE"),
    /**
     * 接通雨刷开关
     */
    WIPER_SWITCH("WIPER_SWITCH"),
    /**
     * 开始记录轨迹
     */
    START_RECORD_TRACK("START_RECORD_TRACK"),
    /**
     * 停止记录轨迹
     */
    STOP_RECORD_TRACK("STOP_RECORD_TRACK"),
    /**
     * 开始轨迹
     */
    START_TRACK("START_TRACK"),
    /**
     * 停止轨迹
     */
    STOP_TRACK("STOP_TRACK"),
    /**
     * 到预置点,该命令的presetIndex不可为空
     */
    GOTO_PRESET("GOTO_PRESET");

    private final String command;

    /**
     * 萤石有方向的Int枚举，因为是海康设备是后来兼容的，接口不变的情况下就只能在这里做个映射来获取海康的操作。
     * 因为{@code direction}并不是海康设备的枚举，直接写成成员变量其实是不合适的，所以只能在这里加了个方法做映射。<br>
     * 萤石操作命令：0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距，16-自动控制。
     * 其中 8-放大，9-缩小，16-自动控制 在海康操作里是没有的。
     */
    public static HikPtzCommandEnum getByYsDirection(final Integer direction) {
        return Optional.ofNullable(direction).filter(u -> u < 12).map(u -> {
            List<HikPtzCommandEnum> list = new ArrayList<>() {
                {
                    add(RIGHT);
                    add(DOWN);
                    add(LEFT);
                    add(RIGHT);
                    add(LEFT_UP);
                    add(LEFT_DOWN);
                    add(RIGHT_UP);
                    add(RIGHT_DOWN);
                    add(null);
                    add(null);
                    add(ZOOM_OUT);
                    add(ZOOM_IN);
                }
            };
            return Optional.ofNullable(list.get(u)).orElseThrow(() ->
                    new IllegalArgumentException("海康设备暂不支持此操作,萤石方向枚举: " + direction));
        }).orElseThrow(() -> new IllegalArgumentException("海康设备暂不支持此操作,萤石方向枚举: " + direction));
    }
}
