package org.orange.mobiletjut.util;

import org.orange.mobiletjut.provider.Contract;
import org.orange.parser.entity.Course;

import android.content.ContentValues;

public class ContentValuesUtils {

    private ContentValuesUtils() {}

    public static ContentValues from(Course course) {
        ContentValues values = new ContentValues();
        if(course.getId() != null)
            values.put(Contract.Course._ID, course.getId());
        if(course.getCode() != null)
            values.put(Contract.Course.CODE, course.getCode());
        if(course.getName() != null)
            values.put(Contract.Course.NAME, course.getName());
        if(course.getTeacherString() != null)
            values.put(Contract.Course.TEACHERS, course.getTeacherString());
        if(course.getCredit() != null)
            values.put(Contract.Course.CREDIT, course.getCredit());
        if(course.getClassNumber() != null)
            values.put(Contract.Course.TEACHING_CLASS, course.getClassNumber());
        if(course.getYear() != null)
            values.put(Contract.Course.YEAR, course.getYear());
        if(course.getSemester() != null)
            values.put(Contract.Course.SEMESTER, course.getSemester());
        if(course.getTestScore() != null)
            values.put(Contract.Course.TEST_SCORE, course.getTestScore());
        if(course.getTotalScore() != null)
            values.put(Contract.Course.TOTAL_SCORE, course.getTotalScore());
        if(course.getKind() != null)
            values.put(Contract.Course.KIND, course.getKind());
        if(course.getNote() != null)
            values.put(Contract.Course.NOTE, course.getNote());
        return values;
    }

    public static ContentValues from(Course.TimeAndAddress timeAndAddress) {
        ContentValues values = new ContentValues();
        values.put(Contract.TimeAndAddress.WEEK, timeAndAddress.getWeek());
        values.put(Contract.TimeAndAddress.DAY, timeAndAddress.getDay());
        values.put(Contract.TimeAndAddress.PERIOD, timeAndAddress.getPeriod());
        if(timeAndAddress.getAddress() != null)
            values.put(Contract.TimeAndAddress.ADDRESS, timeAndAddress.getAddress());
        return values;
    }

}
