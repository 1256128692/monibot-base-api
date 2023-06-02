package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created 2015/12/15
 *
 * @author Liudongdong
 */
public class TimeUtil {
    private static final String MILLI_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String STANDARD_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String MILLI_TIME_FORMAT_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final long MILLI_SECOND_IN_ONE_DAY = 24 * 60 * 60 * 1000;
    public static final long MILLI_SECOND_IN_ONE_HOUR = MILLI_SECOND_IN_ONE_DAY / 24;
    public static final int MILLI_SECOND_IN_ONE_SECOND = 1000;
    public static final long MILLI_SECOND_IN_ONE_MINUTE = MILLI_SECOND_IN_ONE_SECOND * 60;
    public static final long MILLI_SECOND_IN_ONE_WEEK = 7 * MILLI_SECOND_IN_ONE_DAY;

    public static SimpleDateFormat getDefaultFormatter() {
        return new SimpleDateFormat(STANDARD_TIME);
    }

    public static DateTimeFormatter getDefaultDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(STANDARD_TIME);
    }

    public static SimpleDateFormat getMilliDefaultFormatter() {
        return new SimpleDateFormat(MILLI_TIME_FORMAT);
    }



    public static String format(Timestamp time) {
        DateFormat df = CommonVariable.getDefaultDateFormat();
        return df.format(time);
    }

    public static String formatInfluxTimeString(Timestamp time) {
        time = new Timestamp(time.getTime() - 8 * MILLI_SECOND_IN_ONE_HOUR);
        String result = getDefaultFormatter().format(time);
        return result.replace(" ", DbConstant.T) + "Z";
    }

    public static String formatInfluxTimeStringMilli(Timestamp time) {
        time = new Timestamp(time.getTime() - 8 * MILLI_SECOND_IN_ONE_HOUR);
        String result = getMilliDefaultFormatter().format(time);
        return result.replace(" ", DbConstant.T) + "Z";
    }

    public static double minusDay(Timestamp pre, Timestamp next) {
        return (next.getTime() - pre.getTime()) / (MILLI_SECOND_IN_ONE_DAY);
    }

    public static long minusSecond(Timestamp pre, Timestamp next) {
        return (next.getTime() - pre.getTime()) / MILLI_SECOND_IN_ONE_SECOND;
    }

    public static double minusHour(Timestamp pre, Timestamp next) {
        return (next.getTime() - pre.getTime()) / (MILLI_SECOND_IN_ONE_HOUR);
    }

    /**
     * 两个日期之间相差的周数，向下取整
     *
     * @param pre
     * @param next
     * @return
     */
    public static int minusWeek(Timestamp pre, Timestamp next) {
        long milli = next.getTime() - pre.getTime();
        return (int) (milli / MILLI_SECOND_IN_ONE_WEEK);
    }

    public static Timestamp minusMintes(Timestamp time, long minutes) {
        return new Timestamp(time.getTime() - minutes * MILLI_SECOND_IN_ONE_MINUTE);
    }

    public static Timestamp minusHour(Timestamp time, long hours) {
        return new Timestamp(time.getTime() - hours * MILLI_SECOND_IN_ONE_HOUR);
    }

    public static Timestamp minusDay(Timestamp time, long days) {
        long timeMilli = time.getTime();
        return new Timestamp(timeMilli - days * MILLI_SECOND_IN_ONE_DAY);
    }

    public static Timestamp plusHour(Timestamp time, long hours) {
        return new Timestamp(time.getTime() + hours * MILLI_SECOND_IN_ONE_HOUR);
    }

    public static List<Timestamp> getTimePreDayBeginEnd(Timestamp time) {
        LocalDateTime dt = time.toLocalDateTime();
        dt = dt.minusDays(1);
        String str = dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String minStr = str + " 00:00:00";
        String maxStr = str + " 23:59:59";
        List<Timestamp> result = new LinkedList<>();
        result.add(Timestamp.valueOf(minStr));
        result.add(Timestamp.valueOf(maxStr));
        return result;
    }

    public static Timestamp dateTimeParse(String timeStr) {
        return dateTimeParse(timeStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static Timestamp dateTimeParse(String timeStr, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date date = sdf.parse(timeStr);
            return new Timestamp(date.getTime());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 将字符串转换为时间
     *
     * @param timeStr 时间，形如2017-03-20 14:11
     * @return
     */
    public static Timestamp shortTimeParse(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(timeStr);
            return new Timestamp(date.getTime());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Timestamp shortTimeParseDate(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(timeStr);
            return new Timestamp(date.getTime());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Timestamp getDate(Timestamp raw) {
        LocalDateTime localDateTime = raw.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String timeStr = formatter.format(localDateTime) + " 00:00:00";
        return Timestamp.valueOf(timeStr);
    }

    public static Timestamp getHour(Timestamp raw) {
        LocalDateTime localDateTime = raw.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        String timeStr = formatter.format(localDateTime) + ":00:00";
        return Timestamp.valueOf(timeStr);
    }

    public static Timestamp getMaxOrMinTimestamp(Timestamp time, boolean isMax) {
        LocalDateTime localDateTime = time.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String timeStr = null;
        if (isMax)
            timeStr = formatter.format(localDateTime) + " 23:59:59";
        else
            timeStr = formatter.format(localDateTime) + " 00:00:00";
        return Timestamp.valueOf(timeStr);
    }

    /**
     * 时间是否在基准时间的Interval范围内
     *
     * @param time          要测试的时间
     * @param base          基准时间
     * @param intervalMilli 基准时间上下浮动的范围，单位毫秒
     * @return
     */
    public static boolean validInterval(Timestamp time, Timestamp base, long intervalMilli) {
        long baseLong = base.getTime();
        long timeLong = time.getTime();
        long min = baseLong - intervalMilli;
        long max = baseLong + intervalMilli;
        return timeLong >= min && timeLong <= max;
    }

    /**
     * 根据时间戳获取时间
     *
     * @param str 一个整型数
     * @return
     */
    public static LocalDateTime valueOf(String str) {
        return new Timestamp(Long.parseLong(str)).toLocalDateTime();
    }

    public static SimpleDateFormat getRainImageDateFormat() {
        return new SimpleDateFormat("yyyyMMddHHmm");
    }


    public static List<TimeWrapper> getTimeWrapper(Timestamp begin, Timestamp end, String time, int floatHours) {
        end = getMaxOrMinTimestamp(end, true);
        begin = getMaxOrMinTimestamp(begin, false);

        List<TimeWrapper> result = new LinkedList<>();
        int days = (int) minusDay(begin, end) + 1;
        for (int i = 0; i < days; i++) {
            begin = setTimeHour(begin, time);
            if (!(begin.before(end)))
                break;
            TimeWrapper timeWrapper = new TimeWrapper();
            timeWrapper.setBegin(minusHour(begin, floatHours));
            timeWrapper.setEnd(minusHour(begin, -floatHours));
            result.add(timeWrapper);
            begin = TimeUtil.minusDay(begin, -1);
        }
        return result;
    }

    public static Timestamp setTimeHour(Timestamp time, String timeString) {
        LocalDateTime localDateTime = time.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String timeStr = formatter.format(localDateTime) + " " + timeString;
        return Timestamp.valueOf(timeStr);
    }

    public static String getTimeHour(Timestamp time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(time);
    }

    public static String getCNTimeString(Timestamp time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(time);
    }

    public static String formatInfluxDbTime(Timestamp time) {
        time = minusHour(time, 8);
        //2019-12-05T11:09:00.000Z
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String dateString = format.format(time);
        String timeString = timeFormat.format(time);
        return dateString + "T" + timeString + ".000Z";
    }


    public static Timestamp instantToTimestamp(Instant instant) {
        try {
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            DateTimeFormatter formatter = builder.appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter();
            String timeString = formatter.format(ZonedDateTime.ofInstant(instant, ZoneId.of("GMT")).toLocalDateTime().plusHours(8));
            LocalDateTime localDateTime = LocalDateTime.parse(timeString, CommonVariable.getDefaultLocalDateTimeFormat());
            return Timestamp.valueOf(localDateTime);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 判断两个日期是否处于同一天
     *
     * @param thisDay
     * @param time
     * @return
     */
    public static boolean isThisDay(Timestamp thisDay, Timestamp time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(time);
        String now = sdf.format(thisDay);
        if (param.equals(now)) {
            return true;
        }
        return false;
    }


    public static boolean isToday(Timestamp time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        String format = sdf.format(time);
        if (today.equals(format)) {
            return true;
        }
        return false;
    }

    public static boolean isSameDay(Timestamp time1, Timestamp time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr1 = sdf.format(time1);
        String timeStr2 = sdf.format(time2);
        if (timeStr1.equals(timeStr2)) {
            return true;
        }
        return false;
    }

    public static boolean isSameHours(Timestamp time1, Timestamp time2, long hour) {
        Timestamp time = plusHour(time1, hour);
        if (time2.before(time) && time2.after(time1)) {
            return true;
        }
        return false;
    }

    public static boolean isSameThreeHour(Timestamp time1, Timestamp time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        String timeStr1 = sdf.format(time1);
        String timeStr2 = sdf.format(time2);
        if (timeStr1.equals(timeStr2)) {
            return true;
        }
        return false;
    }

    public static boolean isSameOneHour(Timestamp time1, Timestamp time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        String timeStr1 = sdf.format(time1);
        String timeStr2 = sdf.format(time2);
        if (timeStr1.equals(timeStr2)) {
            return true;
        }
        return false;
    }

    public static boolean isInTwentyFourHour(Timestamp time) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        if (minusDay(now, 1l).before(time)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是目标时间前后一小时内
     *
     * @param time
     * @param target
     * @return
     */
    public static boolean isLastHour(Timestamp time, Timestamp target) {
        if (target.after(minusHour(time, 1)) && target.before(minusHour(time, -1))) {
            return true;
        }
        return false;
    }

    public static class TimeWrapper {
        private Timestamp begin;
        private Timestamp end;

        public Timestamp getBegin() {
            return begin;
        }

        public void setBegin(Timestamp begin) {
            this.begin = begin;
        }

        public Timestamp getEnd() {
            return end;
        }

        public void setEnd(Timestamp end) {
            this.end = end;
        }

        @Override
        public String toString() {
            return "TimeWrapper{" +
                    "begin=" + begin +
                    ", end=" + end +
                    '}';
        }
    }


    /**
     * 对于时间分组的数据进行倒序排序
     *
     * @param flag (false:倒序, true:正序)
     * @return
     */
    public static Map<Date, List<Map<String, Object>>> handleTimeSort(List<Map<String, Object>> resultMaps, Boolean flag) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 再将数据以时间进行分组最后返回
        Map<Date, List<Map<String, Object>>> groupedMaps = new HashMap<>();
        for (Map<String, Object> map : resultMaps) {
            String time = (String) map.get(DbConstant.TIME_FIELD);
            try {
                if (groupedMaps.containsKey(dateFormat.parse(time))) {
                    groupedMaps.get(dateFormat.parse(time)).add(map);
                } else {
                    List<Map<String, Object>> list = new ArrayList<>();
                    list.add(map);
                    groupedMaps.put(dateFormat.parse(time), list);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (flag) {
            // 正序
            return MapUtil.sort(groupedMaps, Comparator.naturalOrder());
        } else {
            // 倒序
            return MapUtil.sort(groupedMaps, Comparator.reverseOrder());
        }
    }


    /**
     * 根据开始时间和结束时间去返回list,每个对象是每天的8点到第二天的8点
     * @param begin
     * @param end
     * @return
     */
    public static List<Map<String, Timestamp>> getDailyTimestampList(Timestamp begin, Timestamp end) {
        List<Map<String, Timestamp>> resultList = new ArrayList<>();
        DateTime current = new DateTime(begin);
        while (current.before(end)) {
            Map<String, Timestamp> resultMap = new LinkedHashMap<>();
            resultMap.put("begin", new Timestamp(DateUtil.offsetHour(DateUtil.beginOfDay(current), 8).getTime()));
            current = DateUtil.offsetDay(current, 1);
            resultMap.put("end", new Timestamp(DateUtil.offsetHour(DateUtil.beginOfDay(current), 8).getTime()));
            resultList.add(resultMap);
        }
        Map<String, Timestamp> resultMap = new LinkedHashMap<>();
        resultMap.put("begin", new Timestamp(DateUtil.offsetHour(DateUtil.beginOfDay(current), 8).getTime()));
        resultMap.put("end", new Timestamp(DateUtil.offsetHour(DateUtil.endOfDay(end), 8).getTime()));
        resultList.add(resultMap);
        return resultList;
    }


    /**
     * 判断开始时间和结束时间是否为今日
     * @param begin
     * @param end
     * @return
     */
    public static boolean validateTimestamps(Timestamp begin, Timestamp end) {
        LocalDate currentDate = LocalDate.now();
        LocalDate beginDate = begin.toLocalDateTime().toLocalDate();
        LocalDate endDate = end.toLocalDateTime().toLocalDate();

        return !beginDate.isEqual(currentDate) && !endDate.isEqual(currentDate);
    }


    /**
     * 当density为1的时候,校验begin和end是否为当天
     * 当density为2的时候,校验begin和end是否为当月
     * 当density为3的时候,校验begin和end是否为当年
     * @param begin
     * @param end
     * @return 如果符合条件,则返回true
     */
    public static boolean validateTime(Timestamp begin, Timestamp end, int density) {
        LocalDate beginDate = begin.toLocalDateTime().toLocalDate();
        LocalDate endDate = end.toLocalDateTime().toLocalDate();

        switch (density) {
            case 1: // 当天
                return beginDate.isEqual(LocalDate.now()) || endDate.isEqual(LocalDate.now());
            case 2: // 当月
                LocalDate now = LocalDate.now();
                LocalDate firstDayOfMonth = now.withDayOfMonth(1);
                return  ( beginDate.isAfter(firstDayOfMonth)) || ( endDate.isAfter(firstDayOfMonth));
            case 3: // 当年
                LocalDate firstDayOfYear = LocalDate.now().withDayOfYear(1);
                return (beginDate.isAfter(firstDayOfYear)) || (endDate.isAfter(firstDayOfYear));
            default:
                return false;
        }
    }


    public static Timestamp calculateNewBeginTime(LocalDateTime begin, String density) {
        if (density != null && density.endsWith("h")) {
            int hours = Integer.parseInt(density.substring(0, density.length() - 1));
            begin = begin.plusHours(hours);
        } else if (density != null && density.endsWith("m")) {
            int minutes = Integer.parseInt(density.substring(0, density.length() - 1));
            begin = begin.plusMinutes(minutes);
        } else if (density != null && density.endsWith("d")) {
            // Do nothing for "1d" density
        } else {
            // Default case: do nothing for empty density or unrecognized format
        }

        return Timestamp.valueOf(begin);
    }

}
