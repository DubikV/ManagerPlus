package com.gmail.vanyadubik.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.TrackListContract;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.UserSettings;

public class MobileManagerDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "cooperative";
    private static final int DB_VERSION = 24;

    public MobileManagerDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TrackListContract.TABLE_NAME + "("
                + TrackListContract._ID + " integer primary key AUTOINCREMENT,"
                + TrackListContract.DATE + " integer,"
                + TrackListContract.LATITUDE + " text,"
                + TrackListContract.LONGITUDE + " text,"
                + TrackListContract.IN_CAR + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", TrackListContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + UserSettings.TABLE_NAME + "("
                + UserSettings._ID + " integer primary key AUTOINCREMENT,"
                + UserSettings.USER_SETTING_ID + " text,"
                + UserSettings.SETTING_VALUE + " text,"
                + "UNIQUE (" + TextUtils.join(",", UserSettings.UNIQUE_COLUMNS) + ")"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TrackListContract.TABLE_NAME);
        db.execSQL("drop table if exists " + UserSettings.TABLE_NAME);

        onCreate(db);

    }
}
