package org.orange.mobiletjut.util;

import java.util.Calendar;

public class TimeUtils {
    /**
     * 一周的天数
     */
    public static final int DAYS_PER_WEEK = 7;
    /**
     * 一天的毫秒数
     */
    private static final long MILLIS_IN_DAY = 86_400_000L;
    /**
     * 一周的毫秒数
     */
    private static final long MILLIS_IN_WEEK = DAYS_PER_WEEK * MILLIS_IN_DAY;

    private TimeUtils() {}

    /**
     * @return {@link #getWeeksSince(long, int, long)
     * getWeeksSince(baseWeek, firstDayOfWeek, System.currentTimeMillis())}
     */
    public static int getWeeksSince(long baseWeek, int firstDayOfWeek) {
        return getWeeksSince(baseWeek, firstDayOfWeek, System.currentTimeMillis());
    }
    /**
     * 计算{@code weekNeedCalculated}和{@code baseWeek}间相差的周数
     * @param baseWeek 第0周
     * @param firstDayOfWeek 一周的第一天，见{@link Calendar#setFirstDayOfWeek}
     * @param weekNeedCalculated 要计算的周
     * @return {@code (weekNeedCalculated - baseWeek.firstDayOfItsWeek) / WEEK_LENGTH}
     * @see #adjustToFirstDayOfWeek
     */
    public static int getWeeksSince(long baseWeek, int firstDayOfWeek, long weekNeedCalculated) {
        Calendar base = Calendar.getInstance();
        base.setTimeInMillis(baseWeek);
        base.setFirstDayOfWeek(firstDayOfWeek);
        adjustToFirstDayOfWeek(base);
        return (int) ((weekNeedCalculated - base.getTimeInMillis()) / MILLIS_IN_WEEK);
    }

    /**
     * 把{@code date}调至其所在周的第一天。<br />
     * 根据{@link Calendar#getFirstDayOfWeek}和{@link #DAYS_PER_WEEK}计算。
     * @param date 需调整的日期
     */
    public static void adjustToFirstDayOfWeek(Calendar date) {
        standardizeDate(date);
        //    date -= date.dayOfWeek - mFirstDayOfWeek (delta >= 0)
        // => date += mFirstDayOfWeek - date.dayOfWeek (+= -delta)
        int diff = date.getFirstDayOfWeek() - date.get(Calendar.DAY_OF_WEEK); // diff is -delta
        if(diff > 0) { // but if date.dayOfWeek < mFirstDayOfWeek(delta < 0)
            //    date -= delta + DAYS_PER_WEEK
            // => date += -delta - DAYS_PER_WEEK
            diff -= DAYS_PER_WEEK;
        }
        date.add(Calendar.DAY_OF_MONTH, diff);
    }

    /**
     * 标准化日期。把指定日期的小时、分、秒、毫秒置零。
     * @param date 需标准化的日期
     */
    public static void standardizeDate(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

}
