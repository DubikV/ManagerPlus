package com.gmail.vanyadubik.managerplus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.TrackListContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.LocationPointContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.WaybillContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.VisitContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ClientContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.UserSettings;

public class MobileManagerDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "managerplus";
    private static final int DB_VERSION = 24;

    public MobileManagerDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TrackListContract.TABLE_NAME + "("
                + TrackListContract._ID + " integer primary key AUTOINCREMENT,"
                + TrackListContract.DATE + " integer,"
                + TrackListContract.LATITUDE + " real,"
                + TrackListContract.LONGITUDE + " real,"
                + TrackListContract.IN_CAR + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", TrackListContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + LocationPointContract.TABLE_NAME + "("
                + LocationPointContract._ID + " integer primary key AUTOINCREMENT,"
                + LocationPointContract.LOCATION_DATE + " integer,"
                + LocationPointContract.LOCATION_LATITUDE + " real,"
                + LocationPointContract.LOCATION_LONGITUDE + " real,"
                + LocationPointContract.LOCATION_IN_CAR + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", LocationPointContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + WaybillContract.TABLE_NAME + "("
                + WaybillContract._ID + " integer primary key AUTOINCREMENT,"
                + WaybillContract.WAYBILL_ID + " text,"
                + WaybillContract.WAYBILL_DELETED + " numeric,"
                + WaybillContract.WAYBILL_INDB + " numeric,"
                + WaybillContract.WAYBILL_DATE + " integer,"
                + WaybillContract.WAYBILL_DATE_START + " integer,"
                + WaybillContract.WAYBILL_DATE_END + " integer,"
                + WaybillContract.WAYBILL_POINT_START + " text,"
                + WaybillContract.WAYBILL_POINT_END + " text,"
                + "UNIQUE (" + TextUtils.join(", ", WaybillContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + VisitContract.TABLE_NAME + "("
                + VisitContract._ID + " integer primary key AUTOINCREMENT,"
                + VisitContract.VISIT_ID + " text,"
                + VisitContract.VISIT_DELETED + " numeric,"
                + VisitContract.VISIT_INDB + " numeric,"
                + VisitContract.VISIT_DATE + " integer,"
                + VisitContract.VISIT_DATE_VISIT + " integer,"
                + VisitContract.VISIT_CLIENT + " text,"
                + VisitContract.VISIT_POINT_CREATE + " text,"
                + VisitContract.VISIT_POINT_VISIT + " text,"
                + VisitContract.VISIT_INFORMATION + " text,"
                + "UNIQUE (" + TextUtils.join(", ", VisitContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + ClientContract.TABLE_NAME + "("
                + ClientContract._ID + " integer primary key AUTOINCREMENT,"
                + ClientContract.CLIENT_ID + " text,"
                + ClientContract.CLIENT_DELETED + " numeric,"
                + ClientContract.CLIENT_INDB + " numeric,"
                + ClientContract.CLIENT_NAME + " text,"
                + ClientContract.CLIENT_ADDRESS + " text,"
                + ClientContract.CLIENT_PHONE + " text,"
                + ClientContract.CLIENT_POSITION + " text,"
                + "UNIQUE (" + TextUtils.join(", ", ClientContract.UNIQUE_COLUMNS) + ")"
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
        db.execSQL("drop table if exists " + LocationPointContract.TABLE_NAME);
        db.execSQL("drop table if exists " + WaybillContract.TABLE_NAME);
        db.execSQL("drop table if exists " + VisitContract.TABLE_NAME);
        db.execSQL("drop table if exists " + ClientContract.TABLE_NAME);
        db.execSQL("drop table if exists " + UserSettings.TABLE_NAME);

        onCreate(db);

    }
}