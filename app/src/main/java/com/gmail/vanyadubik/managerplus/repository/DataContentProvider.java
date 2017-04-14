package com.gmail.vanyadubik.managerplus.repository;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.gmail.vanyadubik.managerplus.db.MobileManagerDb;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.AUTHORITY;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ChangingContrack;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.ClientContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.FuelContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.LocationPointContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.TrackListContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.UserSettings;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.UsingCarContrack;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.VisitContract;
import static com.gmail.vanyadubik.managerplus.db.MobileManagerContract.WaybillContract;


public class DataContentProvider extends ContentProvider{

    private MobileManagerDb mobileManagerDb;
    private final UriMatcher URI_MATCHER;


    private static final int TRACK_LIST = 1;
    private static final int TRACK_ID = 2;
    private static final int USER_SETTING_LIST = 3;
    private static final int USER_SETTING_ID = 4;
    private static final int WAYBILL_LIST = 5;
    private static final int WAYBILL_ID = 6;
    private static final int VISIT_LIST = 7;
    private static final int VISIT_ID = 8;
    private static final int CLIENT_LIST = 9;
    private static final int CLIENT_ID = 10;
    private static final int LOCATION_POINT_LIST = 11;
    private static final int LOCATION_POINT_ID = 12;
    private static final int USING_CAR_LIST = 13;
    private static final int USING_CAR_ID = 14;
    private static final int CHANGING_LIST = 15;
    private static final int CHANGING_ID = 16;
    private static final int FUEL_LIST = 17;
    private static final int FUEL_ID = 18;

    public DataContentProvider() {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, TrackListContract.TABLE_NAME, TRACK_LIST);
        URI_MATCHER.addURI(AUTHORITY, TrackListContract.TABLE_NAME + "/#", TRACK_ID);
        URI_MATCHER.addURI(AUTHORITY, UserSettings.TABLE_NAME, USER_SETTING_LIST);
        URI_MATCHER.addURI(AUTHORITY, UserSettings.TABLE_NAME + "/#", USER_SETTING_ID);
        URI_MATCHER.addURI(AUTHORITY, WaybillContract.TABLE_NAME, WAYBILL_LIST);
        URI_MATCHER.addURI(AUTHORITY, WaybillContract.TABLE_NAME + "/#", WAYBILL_ID);
        URI_MATCHER.addURI(AUTHORITY, VisitContract.TABLE_NAME, VISIT_LIST);
        URI_MATCHER.addURI(AUTHORITY, VisitContract.TABLE_NAME + "/#", VISIT_ID);
        URI_MATCHER.addURI(AUTHORITY, ClientContract.TABLE_NAME, CLIENT_LIST);
        URI_MATCHER.addURI(AUTHORITY, ClientContract.TABLE_NAME + "/#", CLIENT_ID);
        URI_MATCHER.addURI(AUTHORITY, LocationPointContract.TABLE_NAME, LOCATION_POINT_LIST);
        URI_MATCHER.addURI(AUTHORITY, LocationPointContract.TABLE_NAME + "/#", LOCATION_POINT_ID);
        URI_MATCHER.addURI(AUTHORITY, UsingCarContrack.TABLE_NAME, USING_CAR_LIST);
        URI_MATCHER.addURI(AUTHORITY, UsingCarContrack.TABLE_NAME + "/#", USING_CAR_ID);
        URI_MATCHER.addURI(AUTHORITY, ChangingContrack.TABLE_NAME, CHANGING_LIST);
        URI_MATCHER.addURI(AUTHORITY, ChangingContrack.TABLE_NAME + "/#", CHANGING_ID);
        URI_MATCHER.addURI(AUTHORITY, FuelContract.TABLE_NAME, FUEL_LIST);
        URI_MATCHER.addURI(AUTHORITY, FuelContract.TABLE_NAME + "/#", FUEL_ID);
    }
    @Override
    public boolean onCreate() {
        mobileManagerDb = new MobileManagerDb(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mobileManagerDb.getReadableDatabase();
        String table = getTable(uri);
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case TRACK_LIST:
                return TrackListContract.CONTENT_TYPE;
            case TRACK_ID:
                return TrackListContract.CONTENT_ITEM_TYPE;
            case USER_SETTING_ID:
                return UserSettings.CONTENT_ITEM_TYPE;
            case USER_SETTING_LIST:
                return UserSettings.CONTENT_TYPE;
            case WAYBILL_LIST:
                return WaybillContract.CONTENT_TYPE;
            case WAYBILL_ID:
                return WaybillContract.CONTENT_ITEM_TYPE;
            case VISIT_LIST:
                return VisitContract.CONTENT_TYPE;
            case VISIT_ID:
                return VisitContract.CONTENT_ITEM_TYPE;
            case CLIENT_LIST:
                return ClientContract.CONTENT_TYPE;
            case CLIENT_ID:
                return ClientContract.CONTENT_ITEM_TYPE;
            case LOCATION_POINT_LIST:
                return LocationPointContract.CONTENT_TYPE;
            case LOCATION_POINT_ID:
                return LocationPointContract.CONTENT_ITEM_TYPE;
            case USING_CAR_LIST:
                return UsingCarContrack.CONTENT_TYPE;
            case USING_CAR_ID:
                return UsingCarContrack.CONTENT_ITEM_TYPE;
            case CHANGING_LIST:
                return ChangingContrack.CONTENT_TYPE;
            case CHANGING_ID:
                return ChangingContrack.CONTENT_ITEM_TYPE;
            case FUEL_LIST:
                return FuelContract.CONTENT_TYPE;
            case FUEL_ID:
                return FuelContract.CONTENT_ITEM_TYPE;

            default:
                throw new RuntimeException("Cannot identify uri " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long inserted;
        SQLiteDatabase db = mobileManagerDb.getWritableDatabase();
        String table = getTable(uri);
        String[] uniqueColumn = getUniqueColumn(uri);

        if (uniqueColumn != null) {
            String selection = buildSelection(uniqueColumn);
            String[] args = buildSelectionArgs(uniqueColumn, values);

            inserted = db.update(table, values, selection, args);
        }

        inserted = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if(inserted != -1){
            return Uri.parse(uri + "/" + inserted);
        }else{
            return null;
        }

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        String table = getTable(uri);

        SQLiteDatabase sqlDB = mobileManagerDb.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            for (ContentValues cv : values) {

                String[] uniqueColumn = getUniqueColumn(uri);
                if (uniqueColumn != null) {
                    String selection = buildSelection(uniqueColumn);
                    String[] args = buildSelectionArgs(uniqueColumn, cv);

                    sqlDB.update(table, cv, selection, args);
                }

                sqlDB.insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            }
            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            numInserted = values.length;
        } finally {
            sqlDB.endTransaction();
        }

        return numInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleted = 0;

        String table = getTable(uri);
        String[] uniqueColumn = getUniqueColumn(uri);

        SQLiteDatabase sqlDB = mobileManagerDb.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            if (uniqueColumn != null) {
                deleted = sqlDB.delete(table, selection, selectionArgs);
            }

            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            return deleted;
        } finally {
            sqlDB.endTransaction();
        }
}

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updated = 0;

        String table = getTable(uri);
        String[] uniqueColumn = getUniqueColumn(uri);

        SQLiteDatabase sqlDB = mobileManagerDb.getWritableDatabase();
        sqlDB.beginTransaction();
        try {
            if (uniqueColumn != null) {
                updated = sqlDB.update(table, values, selection, selectionArgs);
            }

            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            return updated;
        } finally {
            sqlDB.endTransaction();
        }
    }

    private String[] buildSelectionArgs(String[] uniqueColumn, ContentValues values) {
        List<String> args = new ArrayList<>();
        for (String str : uniqueColumn) {
            args.add(values.getAsString(str));
        }

        return args.toArray(new String[args.size()]);
    }

    private String buildSelection(String[] uniqueColumn) {
        StringBuilder builder = new StringBuilder();
        for (String column : uniqueColumn) {
            builder.append(column).append("=? AND ");
        }

        return builder.substring(0, builder.lastIndexOf(" ") - 3);
    }

    private String getTable(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case TRACK_ID:
            case TRACK_LIST:
                return TrackListContract.TABLE_NAME;
            case USER_SETTING_ID:
            case USER_SETTING_LIST:
                return UserSettings.TABLE_NAME;
            case WAYBILL_ID:
            case WAYBILL_LIST:
                return WaybillContract.TABLE_NAME;
            case VISIT_ID:
            case VISIT_LIST:
                return VisitContract.TABLE_NAME;
            case CLIENT_ID:
            case CLIENT_LIST:
                return ClientContract.TABLE_NAME;
            case LOCATION_POINT_ID:
            case LOCATION_POINT_LIST:
                return LocationPointContract.TABLE_NAME;
            case USING_CAR_ID:
            case USING_CAR_LIST:
                return UsingCarContrack.TABLE_NAME;
            case CHANGING_ID:
            case CHANGING_LIST:
                return ChangingContrack.TABLE_NAME;
            case FUEL_ID:
            case FUEL_LIST:
                return FuelContract.TABLE_NAME;
            default:
                throw new RuntimeException("Cannot identify uri " + uri.toString());
        }
    }

    private String[] getUniqueColumn(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case TRACK_ID:
            case TRACK_LIST:
                return TrackListContract.UNIQUE_COLUMNS;
            case USER_SETTING_ID:
            case USER_SETTING_LIST:
                return UserSettings.UNIQUE_COLUMNS;
            case WAYBILL_ID:
            case WAYBILL_LIST:
                return WaybillContract.UNIQUE_COLUMNS;
            case VISIT_ID:
            case VISIT_LIST:
                return VisitContract.UNIQUE_COLUMNS;
            case CLIENT_ID:
            case CLIENT_LIST:
                return ClientContract.UNIQUE_COLUMNS;
            case LOCATION_POINT_ID:
            case LOCATION_POINT_LIST:
                return LocationPointContract.UNIQUE_COLUMNS;
            case USING_CAR_ID:
            case USING_CAR_LIST:
                return UsingCarContrack.UNIQUE_COLUMNS;
            case CHANGING_ID:
            case CHANGING_LIST:
                return ChangingContrack.UNIQUE_COLUMNS;
            case FUEL_ID:
            case FUEL_LIST:
                return FuelContract.UNIQUE_COLUMNS;
            default:
                throw new RuntimeException("Cannot identify uri " + uri.toString());
        }
    }
}
