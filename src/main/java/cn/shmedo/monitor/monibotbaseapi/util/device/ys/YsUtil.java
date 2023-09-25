package cn.shmedo.monitor.monibotbaseapi.util.device.ys;

import cn.hutool.core.date.DateUtil;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class YsUtil {
    /**
     * 获取萤石设备的播放地址
     * <p>
     * ezopen://open.ys7.com/440912260/1.hd.live
     *
     * @param seqNo
     * @param hd
     * @param ysChannelNo
     * @return
     */
    public static String getEzOpenAddress(String seqNo, boolean hd, String ysChannelNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("ezopen://open.ys7.com/");
        builder.append(seqNo);
        builder.append("/");
        builder.append(ysChannelNo);
        if (hd) {
            builder.append(".hd");
        }
        builder.append(".live");
        return builder.toString();
    }

    /**
     * 萤石回放接口，跟播放接口一样的拼接形式
     *
     * @param isCloud   是否是云存储
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @see #getEzOpenAddress(String, boolean, String)
     */
    public static String getEzPlayBackAddress(String seqNo, String ysChannelNo, boolean isCloud, Date beginTime) {
        return "ezopen://open.ys7.com/" + seqNo + "/" + ysChannelNo + (isCloud ? ".cloud" : "") + ".rec?begin="
                + DateUtil.format(beginTime, TimeUtil.YS_PLAY_BACK_TIME_FORMAT);
    }


    /**
     * 获取萤石设备的历史播放地址
     * <p>
     * ezopen://open.ys7.com/440912260/1.hd.live
     *
     * @param seqNo
     * @param ysChannelNo
     * @return
     */
    public static String getEzOpenHistoryAddress(String seqNo, String ysChannelNo, Date begin, Date end) {
        StringBuilder builder = new StringBuilder();
        builder.append("ezopen://open.ys7.com/");
        builder.append(seqNo);
        builder.append("/");
        builder.append(ysChannelNo);
        builder.append(".rec?begin=");
        String beginStr = DateUtil.formatDateTime(begin);
        String endStr = DateUtil.formatDateTime(end);
        String beginTime = beginStr.replaceAll(" ", "").replaceAll(":", "").replaceAll("-", "");
        String endTime = endStr.replaceAll(" ", "").replaceAll(":", "").replaceAll("-", "");
        builder.append(beginTime);
        builder.append("&end=");
        builder.append(endTime);
        return builder.toString();
    }

    public static final List<Integer> VALID_DIRECTIONS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

    public static boolean validDirection(Integer direction) {
        if (direction == null) {
            return false;
        }
        return VALID_DIRECTIONS.contains(direction);
    }
}
