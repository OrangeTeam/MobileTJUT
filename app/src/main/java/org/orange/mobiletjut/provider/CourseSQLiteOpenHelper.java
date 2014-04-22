package org.orange.mobiletjut.provider;

import org.orange.mobiletjut.provider.Contract.Course;
import org.orange.mobiletjut.provider.Contract.TimeAndAddress;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class CourseSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "course.db";
    public static final int DATABASE_VERSION = 1;

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_REAL = " REAL";
    private static final String SEPARATOR_COMMA = ", ";
    private static final String SQL_CREATE_COURSE =
            "CREATE TABLE " + Course.TABLE_NAME + "(" +
            Course._ID + TYPE_INTEGER + " PRIMARY KEY ASC" + SEPARATOR_COMMA +
            Course.CODE + TYPE_TEXT + SEPARATOR_COMMA +
            Course.NAME + TYPE_TEXT + SEPARATOR_COMMA +
            Course.CREDIT + TYPE_INTEGER + SEPARATOR_COMMA +
            Course.KIND + TYPE_TEXT + SEPARATOR_COMMA +
            Course.YEAR + TYPE_INTEGER + SEPARATOR_COMMA +
            Course.SEMESTER + TYPE_INTEGER + SEPARATOR_COMMA +
            Course.TEACHERS + TYPE_TEXT + SEPARATOR_COMMA +
            Course.TEACHING_CLASS + TYPE_TEXT + SEPARATOR_COMMA +
            Course.TEST_SCORE + TYPE_REAL + SEPARATOR_COMMA +
            Course.TOTAL_SCORE + TYPE_REAL + SEPARATOR_COMMA +
            Course.NOTE + TYPE_TEXT + ")";
    private static final String SQL_DROP_COURSE =
            "DROP TABLE IF EXISTS " + Course.TABLE_NAME;
    private static final String SQL_CREATE_TIME_AND_ADDRESS =
            "CREATE TABLE " + TimeAndAddress.TABLE_NAME + "(" +
            TimeAndAddress._ID + TYPE_INTEGER + " PRIMARY KEY ASC" + SEPARATOR_COMMA +
            TimeAndAddress.COURSE_ID + TYPE_INTEGER + SEPARATOR_COMMA +
            TimeAndAddress.WEEK + TYPE_INTEGER + SEPARATOR_COMMA +
            TimeAndAddress.DAY + TYPE_INTEGER + SEPARATOR_COMMA +
            TimeAndAddress.PERIOD + TYPE_INTEGER + SEPARATOR_COMMA +
            TimeAndAddress.ADDRESS + TYPE_TEXT + SEPARATOR_COMMA +
            "CONSTRAINT foreign_key_course_id FOREIGN KEY(" +
            TimeAndAddress.COURSE_ID + ") REFERENCES " + Course.TABLE_NAME +
            "(" + Course._ID + ") ON DELETE CASCADE ON UPDATE CASCADE)";
    private static final String SQL_DROP_TIME_AND_ADDRESS =
            "DROP TABLE IF EXISTS " + TimeAndAddress.TABLE_NAME;

    public CourseSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COURSE);
        db.execSQL(SQL_CREATE_TIME_AND_ADDRESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TIME_AND_ADDRESS);
        db.execSQL(SQL_DROP_COURSE);
        onCreate(db);
    }

}
