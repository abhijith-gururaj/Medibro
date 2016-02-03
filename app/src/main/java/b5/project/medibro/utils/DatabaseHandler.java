package b5.project.medibro.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

import b5.project.medibro.Medication;

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
    public static final String TAG = "Database Handler";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATION);
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

        db.close();
        return hashMap;
    }


}
