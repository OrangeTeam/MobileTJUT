package org.orange.mobiletjut.util;

import org.orange.mobiletjut.provider.Contract;
import org.orange.mobiletjut.provider.CourseProvider;
import org.orange.parser.entity.Course;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class ContentResolverUtils {

    private ContentResolverUtils() {}

    /**
     * 把课程信息（包括其上课时间地点）插入{@link CourseProvider}
     * @param contentResolver 执行插入操作的{@link ContentResolver}
     * @param courses 需要插入的课程列表
     * @throws OperationApplicationException thrown if an application fails.
     * See {@link ContentResolver#applyBatch} for more information.
     * @throws RemoteException thrown if a RemoteException is encountered while attempting
     *   to communicate with a remote provider.
     */
    public static void insertCourse(ContentResolver contentResolver, Collection<Course> courses)
            throws RemoteException, OperationApplicationException {
        List<ContentProviderOperation> operations = new LinkedList<>();
        for(Course course : courses) {
            operations.add(ContentProviderOperation
                    .newInsert(Contract.Course.CONTENT_URI)
                    .withValues(ContentValuesUtils.from(course))
                    .build());
        }
        int index = 0;
        for(Course course : courses) {
            index++;
            for(Course.TimeAndAddress timeAndAddress : course.getTimeAndAddress()) {
                operations.add(ContentProviderOperation
                        .newInsert(Contract.TimeAndAddress.CONTENT_URI)
                        .withValues(ContentValuesUtils.from(timeAndAddress))
                        .withValueBackReference(Contract.TimeAndAddress.COURSE_ID, index)
                        .build());
            }
        }
        contentResolver.applyBatch(Contract.AUTHORITY_COURSE,
                new ArrayList<ContentProviderOperation>(operations));
    }

}
