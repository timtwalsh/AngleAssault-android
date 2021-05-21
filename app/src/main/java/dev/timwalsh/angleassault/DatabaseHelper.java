package dev.timwalsh.angleassault;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "angle_assault";
    private static final String TABLE_SCORES = "table_scores";
    private static final String KEY_ID = "id";
    private static final String PLAYER_NAME = "playerName";
    private static final String SCORE_VALUE = "scoreValue";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + PLAYER_NAME + " TEXT,"
                + SCORE_VALUE + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    public void resetScoresData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCORES);
    }

    void addScore(HighScoreItem score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYER_NAME, score._name);
        values.put(SCORE_VALUE, score._score);
        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    public List<HighScoreItem> getAllScores() {
        List<HighScoreItem> highScoreItemList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SCORES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HighScoreItem highScoreItem = new HighScoreItem();
                highScoreItem.setID(Integer.parseInt(cursor.getString(0)));
                highScoreItem.setName(cursor.getString(1));
                highScoreItem.setScore(cursor.getInt(2));
                highScoreItemList.add(highScoreItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return highScoreItemList;
    }

    public List<HighScoreItem> getHighScoresDesc() {
        List<HighScoreItem> highScoreItemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCORES
                + " ORDER BY " + SCORE_VALUE + " DESC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HighScoreItem highScoreItem = new HighScoreItem();
                highScoreItem.setID(Integer.parseInt(cursor.getString(0)));
                highScoreItem.setName(cursor.getString(1));
                highScoreItem.setScore(cursor.getInt(2));
                highScoreItemList.add(highScoreItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return highScoreItemList;
    }
}
