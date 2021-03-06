package org.orange.mobiletjut.util;

import org.orange.mobiletjut.preference.AccountPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class PreferenceUtils {
    /** 设置项“帐号”的KEY */
    public static final String KEY_PREF_ACCOUNT = "pref_account";
    /** 设置项“帐号”中学号的KEY */
    public static final String KEY_PREF_ACCOUNT_STUDENT_ID = KEY_PREF_ACCOUNT + AccountPreference.USER_ID_SUFFIX;
    /** 设置项“帐号”中密码的KEY */
    public static final String KEY_PREF_ACCOUNT_PASSWORD = KEY_PREF_ACCOUNT + AccountPreference.PASSWORD_SUFFIX;
    /** 设置项“第0周”的KEY */
    public static final String KEY_PREF_ZEROTH_WEEK = "pref_zeroth_week";

    /**
     * 取得第0周的星期一
     * @param context 上下文环境
     * @return 如果设置过开学日期，返回第0周的星期一；如果尚没设置开学时间，返回null
     */
    public static Calendar getMondayOfZeroWeek(Context context) {
        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        if (!sharedPreferences.contains(KEY_PREF_ZEROTH_WEEK)) {
            return null;
        }
        long schoolStarting = sharedPreferences.getLong(KEY_PREF_ZEROTH_WEEK, 0);
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(schoolStarting);
        result.setFirstDayOfWeek(Calendar.MONDAY);
        TimeUtils.adjustToFirstDayOfWeek(result);
        return result;
    }
    /**
     * 根据{@link #getMondayOfZeroWeek(android.content.Context)}，<strong>推断</strong>当前学年。
     * <p><strong>注意</strong>：当前的推断是基于上下学期（只有第一、二学期）的，可能不准确</p>
     * @param context 上下文环境
     * @return 推断结果，如2013表示当前是2013~2014学年
     * @see #getCurrentSemester(android.content.Context)
     */
    public static int getCurrentAcademicYear(Context context) {
        Calendar calendar = getMondayOfZeroWeek(context);
        int year = calendar.get(Calendar.YEAR);
        if(getCurrentSemester(context) != 1)
            year--;
        return year;
    }
    /**
     * 根据{@link #getMondayOfZeroWeek(android.content.Context)}，<strong>推断</strong>当前学期。
     * <p><strong>注意</strong>：当前的推断是基于上下学期（只有第一、二学期）的，可能不准确</p>
     * @param context 上下文环境
     * @return 推断结果，如1表示第一学期
     * @see #getCurrentAcademicYear(android.content.Context)
     */
    public static byte getCurrentSemester(Context context) {
        Calendar calendar = getMondayOfZeroWeek(context);
        int month = calendar.get(Calendar.MONTH) - Calendar.JANUARY + 1;
        month++; // 第0周有可能在假期所在月，为提高判断的准确性，以后一个月判断
        int semester = (month >=3 && month <=8) ? 2 : 1; //TODO 第三小学期怎么判断？
        return (byte) semester;
    }

    /**
     * 取得当前周次。以周一为一周的开始
     * @param context 上下文环境
     * @return 如果设置过开学时间，返回当前周次；如果尚没设置开学时间，返回null
     */
    public static Integer getCurrentWeekNumber(Context context) {
        Calendar c = getMondayOfZeroWeek(context);
        if (c == null) {
            return null;
        } else {
            return TimeUtils.getWeeksSince(c.getTimeInMillis(), Calendar.MONDAY);
        }
    }


    /**
     * 已经设置了帐号（学号和密码）
     * @param context 上下文环境
     * @return 如果学号和密码都已经设置了，返回true；否则返回false
     */
    public static boolean hasSetAccountStudentIDAndPassword(Context context){
        String username = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_PREF_ACCOUNT_STUDENT_ID, null);
        String password = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_PREF_ACCOUNT_PASSWORD, null);
        return username != null && password != null;
    }
    /**
     * 取得账号的学号
     * @param context 上下文环境
     * @return 如果设置过学号，返回此学号；如果尚没设置学号，返回null
     */
    public static String getAccountStudentID(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_PREF_ACCOUNT_STUDENT_ID, null);
    }
    /**
     * 取得账号的密码
     * @param context 上下文环境
     * @return 如果设置过密码，返回此密码；如果尚没设置密码，返回null
     */
    public static String getAccountPassword(Context context){
        String username = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_PREF_ACCOUNT_STUDENT_ID, null);
        String password = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_PREF_ACCOUNT_PASSWORD, null);
        if (password == null) {
            return null;
        } else {
            return AccountPreference
                    .decrypt(AccountPreference.getStoragePassword(username), password);
        }
    }

}
