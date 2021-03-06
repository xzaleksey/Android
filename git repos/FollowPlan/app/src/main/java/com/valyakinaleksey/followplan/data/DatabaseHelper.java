package com.valyakinaleksey.followplan.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.valyakinaleksey.followplan.presentation.model.Period;
import com.valyakinaleksey.followplan.presentation.model.Plan;
import com.valyakinaleksey.followplan.presentation.model.Task;
import com.valyakinaleksey.followplan.util.Constants;
import com.valyakinaleksey.followplan.util.NotificationsUtils;

import org.joda.time.DateTime;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_PLANS = "plans";
    public static final String TABLE_PERIODS = "periods";
    public static final String TABLE_TASKS = "tasks";

    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String NAME = "name";

    public static final String SELECT_MAX = "SELECT MAX";
    public static final String NOTIFICATIONS_CREATE_SCRIPT = "create table " + TABLE_NOTIFICATIONS + " (" + _ID + " integer primary key autoincrement, "
            + NotificationsUtils.DATETIME + " integer DEFAULT 0);";
    private static final String DATABASE_NAME = "mydatabase.db";

    private static final String PLANS_CREATE_SCRIPT = "create table "
            + TABLE_PLANS + " (" + _ID
            + " integer primary key autoincrement, " + NAME
            + " text not null, " + Plan.DATE_CREATED + " integer, " + Plan.ORDER_NUM + " integer, " + Plan.COLOR
            + " integer, " + Plan.CURRENT_TASKS_COUNT + " integer DEFAULT 0, " + Plan.CURRENT_DONE_TASKS_COUNT + " integer DEFAULT 0, " + Plan.TOTAL_TASKS_COUNT + " integer DEFAULT 0, "
            + Plan.TOTAL_DONE_TASKS_COUNT + " integer DEFAULT 0);";

    private static final String PERIODS_CREATE_SCRIPT = "create table "
            + TABLE_PERIODS + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + NAME
            + " text not null, " + Period.DATE_START + " integer, " + Period.DATE_END + " integer, " + Period.DATE_NOTIFICATION + " integer, " + Period.INTERVAL
            + " integer, " + Period.PLAN + " integer DEFAULT 0);";

    private static final String TASKS_CREATE_SCRIPT = "create table "
            + TABLE_TASKS + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + NAME
            + " text not null, " + Task.DATE_CREATED + " integer, " + Task.DATE_NOTIFICATION + " integer, " + Task.ORDER_NUM
            + " integer, " + Task.PLAN + " integer, " + Task.PERIOD + " integer, " + Task.DONE + " integer DEFAULT 0, "
            + Task.DISPOSABLE + " integer DEFAULT 0, " + Task.PRIORITY + " integer);";

    private static DatabaseHelper instance = null;
    private SQLiteDatabase db;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public static long getId(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(PLANS_CREATE_SCRIPT);
            db.execSQL(PERIODS_CREATE_SCRIPT);
            db.execSQL(TASKS_CREATE_SCRIPT);
            db.execSQL(NOTIFICATIONS_CREATE_SCRIPT);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF  EXISTS " + TABLE_PERIODS);
        db.execSQL("DROP TABLE IF  EXISTS " + TABLE_PLANS);
        db.execSQL("DROP TABLE IF  EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    public long createPlan(String name, int order, int color) {
        ContentValues initialValues = createPlanContentValues(name, order, color);
        return createRow(TABLE_PLANS, initialValues);
    }

    public long createPeriod(String name, int interval, long planId, DateTime dateStart, DateTime dateEnd, DateTime dateNotification) {
        ContentValues initialValues = createPeriodContentValues(name, interval, planId, dateStart, dateEnd, dateNotification);
        return createRow(TABLE_PERIODS, initialValues);
    }

    public long createTask(String name, DateTime dateCreated, DateTime dateNotification, int order, long planId, long periodId, int disposable, int priority) {
        ContentValues initialValues = createTaskContentValues(name, dateCreated, dateNotification, order, planId, periodId, disposable, priority);
        Plan.getPlans().get(planId).increaseTasksCount(this);
        return createRow(TABLE_TASKS, initialValues);
    }

    public long createNotification(long dateTime) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(NotificationsUtils.DATETIME, dateTime);
        return createRow(TABLE_NOTIFICATIONS, initialValues);
    }

    public void increasePlanTasksCount(Plan plan) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Plan.CURRENT_TASKS_COUNT, plan.getCurrentTasksCount());
        contentValues.put(Plan.TOTAL_TASKS_COUNT, plan.getTotalTasksCount());
        updatePlan(plan.getId(), contentValues);
        Log.d("MyTag", plan.getCurrentTasksCount() + " " + plan.getTotalTasksCount());
    }

    public void updatePlanTasksCountDone(Plan plan) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Plan.CURRENT_DONE_TASKS_COUNT, plan.getCurrentDoneTasksCount());
        contentValues.put(Plan.TOTAL_DONE_TASKS_COUNT, plan.getTotalDoneTasksCount());
        updatePlan(plan.getId(), contentValues);
        Log.d("MyTag", plan.getCurrentDoneTasksCount() + " " + plan.getTotalDoneTasksCount());
    }

    private ContentValues createTaskContentValues(String name, DateTime dateCreated, DateTime dateNotification, int order, long planId, long periodId, int disposable, int priority) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Task.NAME, name);
        contentValues.put(Task.DATE_CREATED, dateCreated.getMillis());
        if (dateNotification == null) {
            contentValues.put(Task.DATE_NOTIFICATION, 0);
        } else {
            contentValues.put(Task.DATE_NOTIFICATION, dateNotification.getMillis());
        }
        contentValues.put(Task.ORDER_NUM, order);
        contentValues.put(Task.PLAN, planId);
        contentValues.put(Task.PERIOD, periodId);
        contentValues.put(Task.DISPOSABLE, disposable);
        contentValues.put(Task.DONE, 0);
        contentValues.put(Task.PRIORITY, priority);
        return contentValues;
    }

    private long createRow(String tableName, ContentValues initialValues) {
        db = this.getWritableDatabase();
        long row = db.insert(tableName, null, initialValues);
        db.close();
        return row;
    }

    public Cursor getPlan(long rowId) {
        return getRow(TABLE_PLANS, rowId);
    }

    public Cursor getRow(String tableName, long rowId) {
        return db.query(true, tableName,
                null, _ID + "=" + rowId, null,
                null, null, null, null);
    }

    public Cursor getPeriodsByPlanId(long planId) {
        return db.query(true, TABLE_PERIODS,
                null, Period.PLAN + "=" + planId, null,
                null, null, null, null);
    }

    public int updatePlan(long rowId, ContentValues contentValues) {
        return updateRow(TABLE_PLANS, rowId, contentValues);
    }

    public int updatePeriod(long rowId, ContentValues contentValues) {
        return updateRow(TABLE_PERIODS, rowId, contentValues);
    }

    public int updateTask(long rowId, ContentValues contentValues) {
        return updateRow(TABLE_TASKS, rowId, contentValues);
    }

    private int updateRow(String tableName, long rowId, ContentValues contentValues) {
        db = this.getWritableDatabase();
        return db.update(tableName, contentValues, _ID + " = ?",
                new String[]{String.valueOf(rowId)});
    }

    public int getProjectsCount() {
        return getRowCount(TABLE_PLANS);
    }

    private int getRowCount(String tableName) {
        String countQuery = "SELECT  * FROM " + tableName;
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getTaskMaxOrder() {
        return getMaxOrder(TABLE_TASKS);
    }

    public int getPlanMaxOrder() {
        return getMaxOrder(TABLE_PLANS);
    }

    private int getMaxOrder(String tableName) {
        db = getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"MAX(" + Plan.ORDER_NUM + ")"},
                null, null, null, null, null);
        int order = 0;
        if (cursor.moveToFirst()) {
            order = cursor.getInt(0);
        }
        cursor.close();
        return order;
    }

    public void setTaskDone(long id, boolean done) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Task.DONE, done ? 1 : 0);
        updateRow(TABLE_TASKS, id, contentValues);
    }

    public void setPeriodTasksDone(long periodId, boolean done) {
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Task.DONE, done ? 1 : 0);
        db.update(TABLE_TASKS, contentValues, Task.PERIOD + "=" + periodId, null);
    }

    private int getMaxPlanId() {
        db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLANS, null,
                _ID + "=(" + SELECT_MAX + "(" + _ID + "))", null, null, null, null);
        int maxId = -1;
        if (cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
        }
        cursor.close();
        return maxId;
    }

    public void deletePlan(long rowId) {
        db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_PERIODS, Period.PLAN + "=" + rowId, null);
            db.delete(TABLE_TASKS, Task.PLAN + "=" + rowId, null);
            deleteRow(TABLE_PLANS, rowId);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deletePeriod(long rowId) {
        deleteRow(TABLE_PERIODS, rowId);
    }

    // нужен еще 1 метод delete
    public void deleteTask(long rowId) {
        deleteRow(TABLE_TASKS, rowId);
    }

    public void deleteAllNotifications() {
        db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, null, null);
    }

    public void decreasePlanTasksCount(Task task) {
        Plan plan = task.getPlan();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Plan.CURRENT_TASKS_COUNT, plan.getCurrentTasksCount());
        contentValues.put(Plan.TOTAL_TASKS_COUNT, plan.getTotalTasksCount());
        if (task.isDone()) {
            contentValues.put(Plan.CURRENT_DONE_TASKS_COUNT, plan.getCurrentDoneTasksCount());
            contentValues.put(Plan.TOTAL_DONE_TASKS_COUNT, plan.getTotalDoneTasksCount());
            Log.d(Constants.LOG_TAG, plan.getCurrentDoneTasksCount() + " " + plan.getTotalDoneTasksCount());
        }
        updatePlan(plan.getId(), contentValues);
        Log.d(Constants.LOG_TAG, plan.getCurrentTasksCount() + " " + plan.getTotalTasksCount());
    }


    private void deleteRow(String table, long rowId) {
        db = this.getWritableDatabase();
        db.delete(table, _ID + "=" + rowId, null);
    }

    public Cursor getAllPlans() {
        return getAllRows(TABLE_PLANS);
    }

    public Cursor getAllPeriods() {
        return getAllRows(TABLE_PERIODS);
    }

    public Cursor getAllTasks() {
        return getAllRows(TABLE_TASKS);
    }

    public Cursor getAllNotifications() {
        return getAllRows(TABLE_NOTIFICATIONS);
    }

    public boolean checkExistingNotifications(long millis) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATIONS, null, NotificationsUtils.DATETIME + " = " + millis, null, null, null, null);
        boolean exist = false;
        if (cursor.moveToFirst()) {
            exist = true;
        }
        cursor.close();
        return exist;
    }

    private Cursor getAllRows(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(tableName, null, null,
                null, null, null, null);
    }

    private ContentValues createPlanContentValues(String name, int order, int color) {
        ContentValues values = new ContentValues();
        values.put(Plan.NAME, name);
        values.put(Plan.ORDER_NUM, order);
        values.put(Plan.COLOR, color);
        values.put(Plan.DATE_CREATED, System.currentTimeMillis());
        return values;
    }

    public ContentValues createPeriodContentValues(String name, int interval, long projectId, DateTime dateStart, DateTime dateEnd, DateTime dateNotification) {
        ContentValues values = new ContentValues();
        values.put(Period.NAME, name);
        values.put(Period.DATE_START, dateStart.getMillis());
        values.put(Period.INTERVAL, interval);
        values.put(Period.DATE_END, dateEnd.getMillis());
        if (dateNotification != null) {
            values.put(Period.DATE_NOTIFICATION, dateNotification.getMillis());
        }
        values.put(Period.PLAN, projectId);
        return values;
    }

    public void initFromDb() {
        initPlans();
        initPeriods();
        initTasks();
    }

    private void initTasks() {
        Task.initTasks(this);
        Period.fillTasks();
        Plan.fillPeriods();
        Plan.fillTasks();
    }

    public Cursor getAllTasksByDateNotification(long notificationTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "select " + TABLE_TASKS + ".name as tName, " + TABLE_PLANS + ".name as planName, " + TABLE_PERIODS + ".name as periodName "
                + "from " + TABLE_TASKS
                + " inner join " + TABLE_PERIODS
                + " on " + TABLE_TASKS + "." + Task.PERIOD + " = " + TABLE_PERIODS + "." + _ID
                + " inner join " + TABLE_PLANS
                + " on " + TABLE_PERIODS + "." + Period.PLAN + " = " + TABLE_PLANS + "." + _ID
                + " where " + TABLE_TASKS + "." + Task.DATE_NOTIFICATION + " = ?";

        return db.rawQuery(sqlQuery, new String[]{"" + notificationTime});
    }


    private void initPlans() {
        Plan.initPlans(this);
    }

    private void initPeriods() {
        Period.initPeriods(this);
    }

}