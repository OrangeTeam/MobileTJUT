package org.orange.mobiletjut.fragment;

import org.orange.mobiletjut.R;
import org.orange.mobiletjut.provider.Contract;
import org.orange.mobiletjut.util.ContentResolverUtils;
import org.orange.parser.connection.ConnectionAgent;
import org.orange.parser.connection.SSFWWebsiteConnectionAgent;
import org.orange.parser.entity.Course;
import org.orange.parser.parser.SelectedCourseParser;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class CourseFragment extends ListFragment {
    private CursorAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.no_course));

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1,
                null, new String[]{Contract.Course.NAME}, new int[]{android.R.id.text1}, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_course, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh) {
            Toast.makeText(getActivity(), R.string.action_refresh, Toast.LENGTH_LONG).show();
            new UpdateCourseAsyncTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    // These are the Course rows that we will retrieve.
    static final String[] COURSE_PROJECTION = {
            Contract.Course._ID,
            Contract.Course.NAME
    };
    private final LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // only one loader, so we don't care about the ID.
            return new CursorLoader(getActivity(), Contract.Course.CONTENT_URI, COURSE_PROJECTION,
                    null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
    };

    private class UpdateCourseAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionAgent connectionAgent = new SSFWWebsiteConnectionAgent()
                    .setAccount("20106173", "20106173");
            try {
                List<Course> courses = new SelectedCourseParser()
                        .setConnectionAgent(connectionAgent).parse();
                ContentResolverUtils.insertCourse(getActivity().getContentResolver(), courses);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
