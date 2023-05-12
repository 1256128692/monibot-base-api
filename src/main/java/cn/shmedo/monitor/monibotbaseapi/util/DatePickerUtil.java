package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 11:01
 * @desc: 兼容Ant Design Vue DatePicker的工具类
 */
public class DatePickerUtil {
    /**
     * 获取当前时间是今年的第几周
     *
     * @param date 当前时间
     */
    public static int getDatePickerWeekOfYear(@NotNull Date date) {
        DateTime dateTime = DateUtil.date(date);
        int firstDayInWeek = DateUtil.parse(dateTime.year() + "-01-01", "yyyy-MM-dd").dayOfWeek();
        int weekOfYear = (firstDayInWeek >= 5 || firstDayInWeek == 1) ? dateTime.weekOfYear() - 1 : dateTime.weekOfYear();
        return weekOfYear == 0 ? 1 : weekOfYear;
    }
}
