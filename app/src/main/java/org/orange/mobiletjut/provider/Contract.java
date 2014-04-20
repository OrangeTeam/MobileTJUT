package org.orange.mobiletjut.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {

    /**
     * 禁止实例化
     */
    private Contract() {}

    private static final String SCHEMA = "content://";

    public static final String AUTHORITY = "org.orange.mobiletjut.provider";
    private static final String AUTHORITY_COURSE = AUTHORITY + ".course";
    private static final String AUTHORITY_POST = AUTHORITY + ".post";

    public static final class Course implements BaseColumns {

        /**
         * @see org.orange.parser.entity.Course
         */
        private Course() {}

        static final String TABLE_NAME = "course";

        /**课程代码*/
        public static final String CODE = "code";
        /**课程名称*/
        public static final String NAME = "name";
        /**学分*/
        public static final String CREDIT = "credit";
        /**教学班号*/
        public static final String TEACHINGCLASS = "teaching_class";
        /**学年*/
        public static final String YEAR = "year";
        /**学期，如1、2、3分别代表第一、二、三学期*/
        public static final String SEMESTER = "semester";
        /**课程性质*/
        public static final String KIND = "kind";
        /**结课考核成绩*/
        public static final String TESTSCORE = "test_score";
        /**期末总评成绩*/
        public static final String TOTALSCORE = "total_score";
        /**备注*/
        public static final String NOTE = "note";
        /**
         * 课程教师
         * @see org.orange.parser.entity.Course#getTeacherString
         */
        public static final String TEACHERS = "teachers";

        public static final String AUTHORITY = AUTHORITY_COURSE;

        static final String PATH = "course";
        public static final Uri CONTENT_URI =
                new Uri.Builder().scheme(SCHEMA).authority(AUTHORITY).path(PATH).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.org.orange.provider.course";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.org.orange.provider.course";
        public static final String DEFAULT_SORT_ORDER = YEAR + " ASC, " + SEMESTER + " ASC";

    }

    /**
     * @see org.orange.parser.entity.Course.TimeAndAddress
     */
    public static final class TimeAndAddress implements BaseColumns {

        private TimeAndAddress() {}

        static final String TABLE_NAME = "time_and_address";

        /**
         * 对应的课程的ID
         */
        public static final String COURSE_ID = "course_id";
        /**周*/
        public static final String WEEK = "week";
        /**星期，一周中的第几天*/
        public static final String DAY = "day";
        /**第几节课*/
        public static final String PERIOD = "period";
        /**地点*/
        public static final String ADDRESS = "address";

        public static final String AUTHORITY = AUTHORITY_COURSE;

        static final String PATH = "time_and_address";
        public static final Uri CONTENT_URI =
                new Uri.Builder().scheme(SCHEMA).authority(AUTHORITY).path(PATH).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.org.orange.provider.timeandaddress";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.org.orange.provider.timeandaddress";
        public static final String DEFAULT_SORT_ORDER = COURSE_ID + " ASC, " + _ID + " ASC";

    }

}
