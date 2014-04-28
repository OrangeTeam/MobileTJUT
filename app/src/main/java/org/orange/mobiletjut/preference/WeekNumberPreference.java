package org.orange.mobiletjut.preference;

import net.simonvt.calendarview.CalendarView;

import org.orange.mobiletjut.R;
import org.orange.mobiletjut.util.TimeUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class WeekNumberPreference extends DialogPreference {
    private static final long DEFAULT_ZEROTH_WEEK = 0;
    private static final int FIRST_DAY_OF_WEEK = Calendar.MONDAY;
    private static final int WEEK_NUMBER_MAX = 40;
    private static final Integer[] WEEK_NUMBERS = new Integer[WEEK_NUMBER_MAX + 1];
    static { for(int i = 0 ; i <= WEEK_NUMBER_MAX ; i++) WEEK_NUMBERS[i] = i; }

    private long mZerothWeek;

    private CalendarView mCalendarView;

    public WeekNumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPreference(context, attrs);
    }
    public WeekNumberPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPreference(context, attrs);
    }
    private void initPreference(Context context, AttributeSet attrs) {
        setDialogLayoutResource(R.layout.dialog_week_number);
    }

    public void setZerothWeek(long zerothWeek) {
        mZerothWeek = adjustToFirstDayOfWeek(zerothWeek);
    }

    /**
     * 根据{@link #FIRST_DAY_OF_WEEK}，把{@code week}调到其所在周的第一天
     * @return {@code week}所在周的第一天
     */
    private long adjustToFirstDayOfWeek(long week) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(week);
        calendar.setFirstDayOfWeek(FIRST_DAY_OF_WEEK);
        TimeUtils.adjustToFirstDayOfWeek(calendar);
        return calendar.getTimeInMillis();
    }

    /**
     * 把string解析为Long。sting可带一个字母后缀，若string为null返回null
     *
     * @param string 可带一个字母后缀的Long字符串
     * @return string == null ? null : Long形式的string
     */
    private Long parseLong(String string) throws NumberFormatException {
        Long result = null;
        if (string != null) {
            if (string.matches("[+-]?\\d+")) {
                result = Long.valueOf(string);
            } else if (string.matches("[+-]?\\d+[a-zA-Z]")) {
                result = Long.valueOf(string.substring(0, string.length() - 1));
            } else {
                throw new NumberFormatException("数字\"" + string + "\"的格式错误");
            }
        }
        return result;
    }

    @Override
    protected Long onGetDefaultValue(TypedArray a, int index) {
        String date = a.getString(index);
        Long dateLong = null;
        try {
            dateLong = parseLong(date);
        } catch (NumberFormatException e) {
            // dateLong = DEFAULT_ZEROTH_WEEK;
        }
        if(dateLong == null) { // date == null or encounter NumberFormatException
            dateLong = DEFAULT_ZEROTH_WEEK;
        }
        return adjustToFirstDayOfWeek(dateLong);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            setZerothWeek(getPersistedLong(DEFAULT_ZEROTH_WEEK));
        } else {
            // Set default state from the XML attribute
            setZerothWeek((Long) defaultValue);
            persistLong(mZerothWeek);
        }
    }

    @Override
    protected View onCreateDialogView() {
        View dialogView = super.onCreateDialogView();
        //Set CalendarView's attributes
        mCalendarView = (CalendarView) dialogView.findViewById(R.id.calendar_view);
        mCalendarView.setZerothWeek(mZerothWeek);
        //Set WeekNumberSpinner's attributes
        Spinner weekNumberSpinner = (Spinner) dialogView.findViewById(R.id.week_number_spinner);
        weekNumberSpinner.setAdapter(mSpinnerAdapter);
        final int weekNumber = TimeUtils.getWeeksSince(
                mZerothWeek, FIRST_DAY_OF_WEEK, System.currentTimeMillis());
        if (weekNumber >= 0 && weekNumber <= WEEK_NUMBER_MAX) {
            weekNumberSpinner.setSelection(weekNumber);
        }
        weekNumberSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        return dialogView;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            final long newValue = adjustToFirstDayOfWeek(mCalendarView.getZerothWeek());
            // 触发Preference.OnPreferenceChange
            if(!callChangeListener(newValue)) return;
            setZerothWeek(newValue);
            persistLong(newValue);
        }
    }

    private final ArrayAdapter<Integer> mSpinnerAdapter = new ArrayAdapter<Integer> (
            getContext(), android.R.layout.simple_spinner_item, WEEK_NUMBERS) {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setGravity(Gravity.CENTER);
            return view;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setGravity(Gravity.CENTER);
            return view;
        }
    };
    { mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); }

    private final AdapterView.OnItemSelectedListener mOnItemSelectedListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int weekNumber = (Integer) parent.getSelectedItem();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -weekNumber * TimeUtils.DAYS_PER_WEEK);
            calendar.setFirstDayOfWeek(FIRST_DAY_OF_WEEK);
            TimeUtils.adjustToFirstDayOfWeek(calendar);
            mCalendarView.setZerothWeek(calendar.getTimeInMillis());
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

}
