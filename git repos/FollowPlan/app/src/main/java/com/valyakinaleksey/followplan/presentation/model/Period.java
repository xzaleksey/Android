package com.valyakinaleksey.followplan.presentation.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;

import com.valyakinaleksey.followplan.R;
import com.valyakinaleksey.followplan.data.DatabaseHelper;
import com.valyakinaleksey.followplan.presentation.dialogs.TimePreference;

import org.joda.time.DateTime;

import java.util.LinkedHashMap;
import java.util.Map;


public class Period {
    public static final String NAME = "name";
    public static final String DATE_START = "date_start";
    public static final String DATE_END = "date_end";
    public static final String DATE_NOTIFICATION = "date_notification";
    public static final String PLAN = "plan";
    public static final String INTERVAL = "interval";
    private static Map<Long, Period> periods = new LinkedHashMap<>();
    private static Period lastPeriod;
    private final long id;
    private String name;
    private DateTime dateStart;
    private DateTime dateEnd;
    private int interval;
    private DateTime dateNotification;
    private Plan plan;
    private Map<Long, Task> tasks = new LinkedHashMap<>();

    public Period(long id, String name, Plan plan, int interval) {
        this.id = id;
        this.name = name;
        this.plan = plan;
        this.interval = interval;
    }

    public Period(long id, String name, Plan plan, int interval, DateTime dateStart) {
        this.id = id;
        this.name = name;
        this.plan = plan;
        this.interval = interval;
        this.dateStart = dateStart;
    }

    public Period(long id, String name, Plan plan, int interval, DateTime dateStart, DateTime dateEnd, DateTime dateNotification) {
        this.id = id;
        this.name = name;
        this.plan = plan;
        this.interval = interval;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.dateNotification = dateNotification;
    }

    public static void addPeriod(Period period) {
        periods.put(period.getId(), period);
        lastPeriod = period;
    }

    public static void removePeriod(Period period) {
        periods.remove(period.getId());
        lastPeriod = null;
    }

    public static Map<Long, Period> getPeriods() {
        return periods;
    }

    public static void initPeriods(DatabaseHelper databaseHelper) {
        Cursor cursor = databaseHelper.getAllPeriods();
        periods.clear();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            Long dateNotification = cursor.getLong(cursor.getColumnIndex(DATE_NOTIFICATION));
            periods.put(id, new Period(id, cursor.getString(cursor.getColumnIndex(Period.NAME)),
                    Plan.getPlans().get(cursor.getLong(cursor.getColumnIndex(PLAN))),
                    cursor.getInt(cursor.getColumnIndex(INTERVAL)),
                    new DateTime(cursor.getLong(cursor.getColumnIndex(DATE_START))),
                    new DateTime(cursor.getLong(cursor.getColumnIndex(DATE_END))),
                    dateNotification == 0 ? null : new DateTime(dateNotification)
            ));
        }
        cursor.close();
        databaseHelper.close();
    }

    public static void createBasePeriods(Context context, Plan plan) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        long planId = plan.getId();
        int[] intervals = context.getResources().getIntArray(R.array.periods_base_int);
        String[] periodNames = context.getResources().getStringArray(R.array.periods_base_names);
        DateTime dateStart = new DateTime();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String[] timeNotification = sp.getString(context.getString(R.string.time_notification), TimePreference.DEFAULT_NOTIFICATION_VALUE).split(":");
        final int hour = Integer.parseInt(timeNotification[0]);
        final int minute = Integer.parseInt(timeNotification[1]);
        long periodId;
        for (int i = 0; i < intervals.length; i++) {
            int interval = intervals[i];
            DateTime dateEnd = dateStart.withMinuteOfHour(0).withHourOfDay(0).plusDays(interval);
            DateTime dateNotification = dateEnd.minusDays(1).withHourOfDay(hour).
                    withMinuteOfHour(minute);
            periodId = createBasePeriod(databaseHelper, planId, periodNames[i], interval, dateStart, dateEnd, dateNotification);
            Period period = new Period(periodId, periodNames[i], plan, intervals[i], dateStart, dateEnd, dateNotification);
            Period.addPeriod(period);
            plan.getPeriods().put(periodId, period);
        }
        databaseHelper.close();
    }

    private static long createBasePeriod(DatabaseHelper databaseHelper, long planId, String periodName, int interval, DateTime dateStart, DateTime dateEnd, DateTime dateNotification) {
        return databaseHelper.createPeriod(periodName, interval, planId, dateStart, dateEnd, dateNotification);
    }

    public static void fillTasks() {
        for (Map.Entry<Long, Task> taskEntry : Task.getTasks().entrySet()) {
            Task task = taskEntry.getValue();
            periods.get(task.getPeriod().getId()).tasks.put(taskEntry.getKey(), task);
        }
    }

    public static Period getLastPeriod() {
        return lastPeriod;
    }

    public static void setLastPeriod(Period lastPeriod) {
        Period.lastPeriod = lastPeriod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(DateTime dateStart) {
        this.dateStart = dateStart;
    }

    public DateTime getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(DateTime dateEnd) {
        this.dateEnd = dateEnd;
    }

    public DateTime getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(DateTime dateNotification) {
        this.dateNotification = dateNotification;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public long getId() {
        return id;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Map<Long, Task> getTasks() {
        return tasks;
    }

    public Task removeTask(Task task) {
        return tasks.remove(task.getId());
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public int getTasksDoneCount() {
        int count = 0;
        for (Task task : tasks.values()) {
            if (task.isDone()) {
                count++;
            }
        }
        return count;
    }

    public void updateDates(DatabaseHelper databaseHelper, int diff) {
        final DateTime dateStart = getDateStart().plusDays(getInterval() * diff);
        final DateTime dateEnd = getDateEnd().plusDays(getInterval() * diff);
        ContentValues contentValues = new ContentValues();
        setDateStart(dateStart);
        setDateEnd(dateEnd);
        contentValues.put(DATE_START, dateStart.getMillis());
        contentValues.put(DATE_END, dateEnd.getMillis());
        databaseHelper.updatePeriod(getId(), contentValues);
        if (getDateNotification().getMillis() != 0) {
            setDateNotification(getDateNotification().plusDays(getInterval() * diff));
        }
    }
}
