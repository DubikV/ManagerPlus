package com.gmail.vanyadubik.mobilemanager.repository;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.gmail.vanyadubik.mobilemanager.db.MobileManagerDb;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.AUTHORITY;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.TrackListContract;
import static com.gmail.vanyadubik.mobilemanager.db.MobileManagerContract.UserSettings;

public class DataContentProvider extends ContentProvider{

    private MobileManagerDb mobileManagerDb;
    private final UriMatcher URI_MATCHER;


    private static final int TRACK_LIST = 1;
    private static final int TRACK_ID = 2;
    private static final int USER_SETTING_LIST = 3;
    private static final int USER_SETTING_ID = 4;

    public DataContentProvider() {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, TrackListContract.TABLE_NAME, TRACK_LIST);
        URI_MATCHER.addURI(AUTHORITY, TrackListContract.TABLE_NAME + "/#", TRACK_ID);
        URI_MATCHER.addURI(AUTHORITY, UserSettings.TABLE_NAME, USER_SETTING_LIST);
        URI_MATCHER.addURI(AUTHORITY, UserSettings.TABLE_NAME + "/#", USER_SETTING_ID);
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

            default:
                throw new RuntimeException("Cannot identify uri " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mobileManagerDb.getWritableDatabase();
        String table = getTable(uri);

        String[] uniqueColumn = getUniqueColumn(uri);
        if (uniqueColumn != null) {
            String selection = buildSelection(uniqueColumn);
            String[] args = buildSelectionArgs(uniqueColumn, values);

            db.update(table, values, selection, args);
        }

        return db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1 ? uri : null;
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
            default:
                throw new RuntimeException("Cannot identify uri " + uri.toString());
        }
    }
}
