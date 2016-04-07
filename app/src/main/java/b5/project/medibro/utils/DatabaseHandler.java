package b5.project.medibro.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import b5.project.medibro.receivers.Medication;

/**
 * Created by Abhijith on 1/27/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "medibro_local";
    public static final String TABLE_MEDICATION = "medication";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String INTERVALS = "INTERVALS";
    public static final String REMINDER_TIMES = "REMINDER_TIMES";
    public static final String START_DATE = "START_DATE";
    public static final String DURATION = "DURATION";
    public static final String ADDITIONAL_NOTES = "ADDITIONAL_NOTES";

    public static final String TABLE_ALARMS = "medication_alarms";
    public static final String MEDICATION_ID = "medication_id";
    public static final String ALARMS_SET_ID = "set_id";
    public static final String ALARMS_CANCEL_ID = "cancellation_id";
    public static final String TAG = "Database Handler";

    public static final String TABLE_CHAT_USERS = "chat_users";
    public static final String FROM_USER_ID = "from_user_id";
    public static final String TO_USER_ID = "to_user_id";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String CREATE_MEDICATION_TABLE = "CREATE TABLE " + TABLE_MEDICATION + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + NAME + " TEXT NOT NULL,"
                + INTERVALS + " INTEGER NOT NULL,"
                + REMINDER_TIMES + " TEXT NOT NULL,"
                + START_DATE + " TEXT NOT NULL,"
                + DURATION + " TEXT NOT NULL,"
                + ADDITIONAL_NOTES + " TEXT" + ")";
        db.execSQL(CREATE_MEDICATION_TABLE);
        Log.d(TAG, TABLE_MEDICATION + " table created");

        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS + " ("
                + ALARMS_SET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + ALARMS_CANCEL_ID + " INTEGER UNIQUE, "
                + MEDICATION_ID + " INTEGER NOT NULL " + ")";
        db.execSQL(CREATE_ALARMS_TABLE);
        Log.d(TAG, TABLE_ALARMS + " table created");

        String CREATE_CHAT_USERS_TABLE = "CREATE TABLE " + TABLE_CHAT_USERS + " ("
                + FROM_USER_ID + " TEXT NOT NULL,"
                + TO_USER_ID + " TEXT UNIQUE NOT NULL " + ")";
        db.execSQL(CREATE_CHAT_USERS_TABLE);
        Log.d(TAG, TABLE_CHAT_USERS + " table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_USERS);
        // Create tables again
        onCreate(db);
    }

    public long addMedication(String name, int intervals, String reminder_times,
                              String start_date, String duration, String additional_notes) {

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "Inserting: " + name + intervals + reminder_times + start_date + duration
                + additional_notes);

        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(INTERVALS, intervals);
        cv.put(REMINDER_TIMES, reminder_times);
        cv.put(START_DATE, start_date);
        cv.put(DURATION, duration);
        cv.put(ADDITIONAL_NOTES, additional_notes);

        long id = db.insert(TABLE_MEDICATION, null, cv);
        db.close();
        return id;
    }

    public HashMap<Integer, Medication> getMedicationNames() {
        HashMap<Integer, Medication> hashMap = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + ID + " , " + NAME + " FROM " + TABLE_MEDICATION;
        Log.d(TAG, "selectQuery: " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int counter = 0;
        while (!cursor.isAfterLast()) {
            Medication medication = new Medication();
            medication.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            medication.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            hashMap.put(counter, medication);
            Log.d(TAG, "Fetching: " + medication.getName());
            counter++;
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return hashMap;
    }

    public HashMap<Integer, Medication> getMedicationDetails() {

        HashMap<Integer, Medication> hashMap = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_MEDICATION;
        Log.d(TAG, "selectQuery: " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int counter = 0;

        while (!cursor.isAfterLast()) {
            Medication medication = new Medication();
            medication.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            medication.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            medication.setIntervals(cursor.getString(cursor.getColumnIndex(INTERVALS)));
            medication.setDuration(cursor.getString(cursor.getColumnIndex(DURATION)));
            medication.setReminder_times(cursor.getString(cursor.getColumnIndex(REMINDER_TIMES)));
            medication.setStart_date(cursor.getString(cursor.getColumnIndex(START_DATE)));
            medication.setNotes(cursor.getString(cursor.getColumnIndex(ADDITIONAL_NOTES)));


            hashMap.put(counter, medication);
            Log.d(TAG, "Fetching: " + medication.getName() + medication.getIntervals());
            counter++;
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return hashMap;
    }

    public int getLatestAlarmId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int lastId = 0;
        String query = "SELECT " + ALARMS_SET_ID + " from " + TABLE_ALARMS +
                " order by " + ALARMS_SET_ID +
                " DESC limit 1";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            lastId = c.getInt(0);
            //The 0 is the column index, we only have 1 column, so the index is 0
        }
        c.close();
        db.close();
        return lastId + 1;
    }

    public void addAlarm(long medId, int setId, int cancelId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MEDICATION_ID, medId);
        contentValues.put(ALARMS_CANCEL_ID, cancelId);
        contentValues.put(ALARMS_SET_ID, setId);
        db.insert(TABLE_ALARMS, null, contentValues);
        Log.d(TAG, "Inserting: " + medId + " " + setId + " " + cancelId);
        db.close();
    }

    public void deleteMedication(int medId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int row = db.delete(TABLE_MEDICATION, ID + "=" + medId, null);
        Log.d(TAG, "deleted " + row + " row");
        db.close();
    }

    public void deleteAlarms(int medId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ALARMS, MEDICATION_ID + "=" + medId, null);
        Log.d(TAG, "deleted " + rows + " rows");
        db.close();
    }

    public ArrayList<Integer> getMedicationSetIds(int medId) {
        ArrayList<Integer> setIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ALARMS_SET_ID + " FROM " + TABLE_ALARMS + " WHERE " +
                MEDICATION_ID + "=" + medId;
        Log.d(TAG, query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int setId = cursor.getInt(cursor.getColumnIndex(ALARMS_SET_ID));
            setIds.add(setId);
            Log.d(TAG, "MedId: " + medId + " setId: " + setId);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return setIds.isEmpty() ? null : setIds;
    }

    public void addChatUsers(String id1, String id2) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(FROM_USER_ID, id1);
        cv.put(TO_USER_ID, id2);

        db.insert(TABLE_CHAT_USERS, null, cv);
        Log.d(TAG, "Inserting: " + id1 + " , " + id2);
        db.close();
    }

    public HashMap<Integer, String> getBuddyList(String fromId) {
        SQLiteDatabase db = this.getReadableDatabase();

        HashMap<Integer, String> hashMap = new HashMap<>();

        String[] columns = {TO_USER_ID};//colums name that you select

        Cursor res = db.query(TABLE_CHAT_USERS, columns, FROM_USER_ID + "=?",
                new String[]{fromId}, null, null, null);
        res.moveToFirst();
        int counter = 0;
        while (!res.isAfterLast()) {

            String toUserId = res.getString(res.getColumnIndex(TO_USER_ID));
            hashMap.put(counter, toUserId);
            Log.d(TAG, toUserId);
            res.moveToNext();
            counter++;
        }

        res.close();
        db.close();
        return hashMap;
    }

    public Boolean hasBuddyRow(String toId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String[] columns = {FROM_USER_ID};//colums name that you select

        try {
            cursor = db.query(TABLE_CHAT_USERS, columns, TO_USER_ID + "=?",
                    new String[]{toId}, null, null, null);
        } catch (Exception e) {
            return false;
        }

//        if(cursor.moveToFirst())
//            return false;
//
//        Boolean b=cursor.getString(cursor.getColumnIndex(FROM_USER_ID))==null;

        Boolean b = cursor.moveToFirst();
        cursor.close();
        db.close();
        return b;
    }
}
