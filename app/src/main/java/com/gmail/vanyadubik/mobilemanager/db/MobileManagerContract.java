package com.gmail.vanyadubik.mobilemanager.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MobileManagerContract {

    private static final String CONTENT = "content://";
    public static final String AUTHORITY = "com.avatlantik.mbmanager.dbProvider";

    public static final Uri CONTENT_URI
            = Uri.parse(CONTENT + AUTHORITY);

    public static final class TrackListContract implements BaseColumns{

        public static final String TABLE_NAME = "track_list";

        public static final String DATE = "date";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String IN_CAR = "incar";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        MobileManagerContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, DATE, LATITUDE, LONGITUDE, IN_CAR};

        public static final String[] UNIQUE_COLUMNS =
                {DATE};

        public static final String DEFAULT_SORT_ORDER = DATE + " ASC";


    }

    public static final class UserSettings implements BaseColumns {

        public static final String TABLE_NAME = "user_settings";

        public static final String USER_SETTING_ID = "setting_id";
        public static final String SETTING_VALUE = "value";

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        MobileManagerContract.CONTENT_URI,
                        TABLE_NAME);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd."
                        + AUTHORITY + "."
                        + TABLE_NAME;

        public static final String[] PROJECTION_ALL =
                {_ID, USER_SETTING_ID, SETTING_VALUE};

        public static final String[] UNIQUE_COLUMNS =
                {USER_SETTING_ID};

        public static final String DEFAULT_SORT_ORDER = SETTING_VALUE + " ASC";

    }

}
