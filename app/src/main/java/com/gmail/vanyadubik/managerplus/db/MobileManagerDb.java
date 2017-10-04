package com.gmail.vanyadubik.managerplus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ClientContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.LocationPointContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.TrackListContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.UserSettings;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.VisitContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.WaybillContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.UsingCarContrack;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ChangingContrack;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.FuelContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.PhotoContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.VisitEventContract;

public class MobileManagerDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "managerplus";
    private static final int DB_VERSION = 26;

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
                + TrackListContract.UNLOADED + " numeric,"
                + "UNIQUE (" + TextUtils.join(", ", TrackListContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + LocationPointContract.TABLE_NAME + "("
                + LocationPointContract._ID + " integer primary key AUTOINCREMENT,"
                + LocationPointContract.LOCATION_DATE + " integer,"
                + LocationPointContract.LOCATION_LATITUDE + " real,"
                + LocationPointContract.LOCATION_LONGITUDE + " real,"
                + "UNIQUE (" + TextUtils.join(", ", LocationPointContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + WaybillContract.TABLE_NAME + "("
                + WaybillContract._ID + " integer primary key AUTOINCREMENT,"
                + WaybillContract.EXTERNAL_ID + " text,"
                + WaybillContract.DELETED + " numeric,"
                + WaybillContract.INDB + " numeric,"
                + WaybillContract.DATE + " integer,"
                + WaybillContract.DATE_START + " integer,"
                + WaybillContract.DATE_END + " integer,"
                + WaybillContract.POINT_START + " integer,"
                + WaybillContract.POINT_END + " integer,"
                + WaybillContract.ODOMETER_START + " integer,"
                + WaybillContract.ODOMETER_END + " integer,"
                + "UNIQUE (" + TextUtils.join(", ", WaybillContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + VisitContract.TABLE_NAME + "("
                + VisitContract._ID + " integer primary key AUTOINCREMENT,"
                + VisitContract.EXTERNAL_ID + " text,"
                + VisitContract.DELETED + " numeric,"
                + VisitContract.INDB + " numeric,"
                + VisitContract.DATE + " integer,"
                + VisitContract.DATE_VISIT + " integer,"
                + VisitContract.CLIENT + " text,"
                + VisitContract.POINT_CREATE + " integer,"
                + VisitContract.POINT_VISIT + " integer,"
                + VisitContract.VISIT_TYPE + " text,"
                + VisitContract.INFORMATION + " text,"
                + "UNIQUE (" + TextUtils.join(", ", VisitContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + ClientContract.TABLE_NAME + "("
                + ClientContract._ID + " integer primary key AUTOINCREMENT,"
                + ClientContract.EXTERNAL_ID + " text,"
                + ClientContract.DELETED + " numeric,"
                + ClientContract.INDB + " numeric,"
                + ClientContract.NAME + " text,"
                + ClientContract.ADDRESS + " text,"
                + ClientContract.PHONE + " text,"
                + ClientContract.POSITION + " integer,"
                + "UNIQUE (" + TextUtils.join(", ", ClientContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + UserSettings.TABLE_NAME + "("
                + UserSettings._ID + " integer primary key AUTOINCREMENT,"
                + UserSettings.USER_SETTING_ID + " text,"
                + UserSettings.SETTING_VALUE + " text,"
                + "UNIQUE (" + TextUtils.join(",", UserSettings.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + ChangingContrack.TABLE_NAME + "("
                + ChangingContrack._ID + " integer primary key AUTOINCREMENT,"
                + ChangingContrack.MANE_ELEMENT + " text,"
                + ChangingContrack.ELEMENT_ID + " text,"
                + "UNIQUE (" + TextUtils.join(",", ChangingContrack.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + UsingCarContrack.TABLE_NAME + "("
                + UsingCarContrack._ID + " integer primary key AUTOINCREMENT,"
                + UsingCarContrack.DATE + " text,"
                + UsingCarContrack.INCAR + " numeric,"
                + "UNIQUE (" + TextUtils.join(",", UsingCarContrack.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + FuelContract.TABLE_NAME + "("
                + FuelContract._ID + " integer primary key AUTOINCREMENT,"
                + FuelContract.EXTERNAL_ID + " text,"
                + FuelContract.DELETED + " numeric,"
                + FuelContract.INDB + " numeric,"
                + FuelContract.DATE + " integer,"
                + FuelContract.TYPE_FUEL + " text,"
                + FuelContract.TYPE_PAYMENT + " text,"
                + FuelContract.LITRES + " real,"
                + FuelContract.MONEY + " real,"
                + FuelContract.POINT_CREATE + " integer,"
                + "UNIQUE (" + TextUtils.join(",", FuelContract.UNIQUE_COLUMNS) + ")"
                + ");");

        db.execSQL("create table " + PhotoContract.TABLE_NAME + "("
                + PhotoContract._ID + " integer primary key AUTOINCREMENT,"
                + PhotoContract.EXTERNAL_ID + " text,"
                + PhotoContract.DELETED + " numeric,"
                + PhotoContract.INDB + " numeric,"
                + PhotoContract.NAME + " text,"
                + PhotoContract.HOLDERNAME + " text,"
                + PhotoContract.HOLDERID + " text,"
                + PhotoContract.DATE + " integer,"
                + PhotoContract.INFO + " text,"
                + "UNIQUE (" + TextUtils.join(",", PhotoContract.UNIQUE_COLUMNS) + ")"
                + ");");
        db.execSQL("create table " + VisitEventContract.TABLE_NAME + "("
                + VisitEventContract._ID + " integer primary key AUTOINCREMENT,"
                + VisitEventContract.VISIT_ID + " integer,"
                + VisitEventContract.EVENT_ID + " integer,"
                + "UNIQUE (" + TextUtils.join(",", VisitEventContract.UNIQUE_COLUMNS) + ")"
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
        db.execSQL("drop table if exists " + ChangingContrack.TABLE_NAME);
        db.execSQL("drop table if exists " + UsingCarContrack.TABLE_NAME);
        db.execSQL("drop table if exists " + FuelContract.TABLE_NAME);
        db.execSQL("drop table if exists " + PhotoContract.TABLE_NAME);
        db.execSQL("drop table if exists " + VisitEventContract.TABLE_NAME);

        onCreate(db);

    }
}
