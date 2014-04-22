package org.orange.mobiletjut.provider;

import org.orange.mobiletjut.provider.Contract.Course;
import org.orange.mobiletjut.provider.Contract.TimeAndAddress;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

public class CourseProvider extends ContentProvider {
    private static final int COURSES = 0x1;
    private static final int COURSES_ID = 0x2;
    private static final int TIME_AND_ADDRESSES = 0x11;
    private static final int TIME_AND_ADDRESSES_ID = 0x12;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(Course.AUTHORITY, Course.PATH, COURSES);
        URI_MATCHER.addURI(Course.AUTHORITY, Course.PATH + "/#", COURSES_ID);
        URI_MATCHER.addURI(TimeAndAddress.AUTHORITY, TimeAndAddress.PATH, TIME_AND_ADDRESSES);
        URI_MATCHER.addURI(TimeAndAddress.AUTHORITY, TimeAndAddress.PATH + "/#", TIME_AND_ADDRESSES_ID);
    }

    private SQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new CourseSQLiteOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case COURSES:
                return Course.CONTENT_TYPE;
            case COURSES_ID:
                return Course.CONTENT_ITEM_TYPE;
            case TIME_AND_ADDRESSES:
                return TimeAndAddress.CONTENT_TYPE;
            case TIME_AND_ADDRESSES_ID:
                return TimeAndAddress.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        selection = appendIdIfNecessary(uri, selection);
        String table = getTable(uri);
        if(TextUtils.isEmpty(sortOrder)) {
            if(Course.TABLE_NAME.equals(table)) {
                sortOrder = Course.DEFAULT_SORT_ORDER;
            } else if(TimeAndAddress.TABLE_NAME.equals(table)) {
                sortOrder = TimeAndAddress.DEFAULT_SORT_ORDER;
            } else {
                throw new AssertionError("unknown table: " + table);
            }
        }
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                table, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = URI_MATCHER.match(uri);
        if(match != COURSES && match != TIME_AND_ADDRESSES)
            throw new IllegalArgumentException("Illegal URI: " + uri);
        long id = mOpenHelper.getWritableDatabase().insert(getTable(uri), null, values);
        if(id != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        selection = appendIdIfNecessary(uri, selection);
        int affected = mOpenHelper.getWritableDatabase().delete(getTable(uri), selection, selectionArgs);
        if(affected > 0) getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        selection = appendIdIfNecessary(uri, selection);
        int affected =  mOpenHelper.getWritableDatabase().update(getTable(uri), values, selection, selectionArgs);
        if(affected > 0) getContext().getContentResolver().notifyChange(uri, null);
        return affected;
    }

    private static String appendIdIfNecessary(Uri uri, String selection) {
        final String appendWhere = "(%s) AND %s = %d";
        switch (URI_MATCHER.match(uri)) {
            case COURSES_ID:
                return String.format(appendWhere, selection, Course._ID, ContentUris.parseId(uri));
            case TIME_AND_ADDRESSES_ID:
                return String.format(appendWhere, selection, TimeAndAddress._ID, ContentUris.parseId(uri));
            default:
                return selection;
        }
    }
    private static String getTable(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case COURSES: case COURSES_ID:
                return Course.TABLE_NAME;
            case TIME_AND_ADDRESSES: case TIME_AND_ADDRESSES_ID:
                return TimeAndAddress.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

}
