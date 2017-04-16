package com.gmail.vanyadubik.managerplus.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class MobileManagerContract {

    private static final String CONTENT = "content://";
    public static final String AUTHORITY = "com.gmail.vanyadubik.mbmanager.dbProvider";

    public static final Uri CONTENT_URI
            = Uri.parse(CONTENT + AUTHORITY);

    public static final class TrackListContract implements BaseColumns{

        public static final String TABLE_NAME = "track_list";

        public static final String DATE = "date";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String UNLOADED = "unloaded";

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
                {_ID, DATE, LATITUDE, LONGITUDE, UNLOADED};

        public static final String[] UNIQUE_COLUMNS =
                {DATE};

        public static final String DEFAULT_SORT_ORDER = DATE + " ASC";


    }

    public static final class WaybillContract implements BaseColumns{

        public static final String TABLE_NAME = "waybill_list";

        public static final String WAYBILL_ID = "waybill_id";
        public static final String WAYBILL_DELETED = "deleted";
        public static final String WAYBILL_INDB = "incdb";
        public static final String WAYBILL_DATE = "date";
        public static final String WAYBILL_DATE_START = "date_start";
        public static final String WAYBILL_DATE_END = "date_end";
        public static final String WAYBILL_POINT_START = "point_start";
        public static final String WAYBILL_POINT_END = "point_end";
        public static final String WAYBILL_ODOMETER_START = "odometer_start";
        public static final String WAYBILL_ODOMETER_END = "odometer_end";

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
                {_ID, WAYBILL_ID, WAYBILL_DELETED, WAYBILL_INDB, WAYBILL_DATE, WAYBILL_DATE_START,
                        WAYBILL_DATE_END, WAYBILL_POINT_START, WAYBILL_POINT_END,
                        WAYBILL_ODOMETER_START, WAYBILL_ODOMETER_END};

        public static final String[] UNIQUE_COLUMNS =
                {WAYBILL_ID};

        public static final String DEFAULT_SORT_ORDER = WAYBILL_DATE_START + " ASC";


    }

    public static final class VisitContract implements BaseColumns{

        public static final String TABLE_NAME = "visit_list";

        public static final String VISIT_ID = "visit_id";
        public static final String VISIT_DELETED = "deleted";
        public static final String VISIT_INDB = "incdb";
        public static final String VISIT_DATE = "date";
        public static final String VISIT_DATE_VISIT = "date_visit";
        public static final String VISIT_CLIENT = "visit_client";
        public static final String VISIT_POINT_CREATE = "point_create";
        public static final String VISIT_POINT_VISIT = "point_visit";
        public static final String VISIT_TYPE = "visit_type";
        public static final String VISIT_INFORMATION = "visit_info";

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
                {_ID, VISIT_ID, VISIT_DELETED, VISIT_INDB, VISIT_DATE, VISIT_DATE_VISIT,
                        VISIT_CLIENT, VISIT_POINT_CREATE, VISIT_POINT_VISIT, VISIT_TYPE,
                        VISIT_INFORMATION};

        public static final String[] UNIQUE_COLUMNS =
                {VISIT_ID};

        public static final String DEFAULT_SORT_ORDER = VISIT_DATE + " ASC";


    }

    public static final class ClientContract implements BaseColumns{

        public static final String TABLE_NAME = "client_list";

        public static final String CLIENT_ID = "client_id";
        public static final String CLIENT_DELETED = "deleted";
        public static final String CLIENT_INDB = "incdb";
        public static final String CLIENT_NAME = "name";
        public static final String CLIENT_ADDRESS = "address";
        public static final String CLIENT_PHONE = "phone";
        public static final String CLIENT_POSITION = "position";

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
                {_ID, CLIENT_ID, CLIENT_DELETED, CLIENT_INDB, CLIENT_NAME, CLIENT_ADDRESS,
                        CLIENT_PHONE, CLIENT_POSITION};

        public static final String[] UNIQUE_COLUMNS =
                {CLIENT_ID};

        public static final String DEFAULT_SORT_ORDER = CLIENT_NAME + " ASC";

    }

    public static final class LocationPointContract implements BaseColumns{

        public static final String TABLE_NAME = "location_point";

        public static final String LOCATION_DATE = "date";
        public static final String LOCATION_LATITUDE = "latitude";
        public static final String LOCATION_LONGITUDE = "longitude";

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
                {_ID, LOCATION_DATE, LOCATION_LATITUDE, LOCATION_LONGITUDE};

        public static final String[] UNIQUE_COLUMNS =
                {_ID};

        public static final String DEFAULT_SORT_ORDER = LOCATION_DATE + " ASC";

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

    public static final class ChangingContrack implements BaseColumns {

        public static final String TABLE_NAME = "changed_element";

        public static final String MANE_ELEMENT = "name";
        public static final String ELEMENT_ID = "externa_id";

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
                {_ID, MANE_ELEMENT, ELEMENT_ID};

        public static final String[] UNIQUE_COLUMNS =
                {ELEMENT_ID};

        public static final String DEFAULT_SORT_ORDER = ELEMENT_ID + " ASC";

    }

    public static final class UsingCarContrack implements BaseColumns {

        public static final String TABLE_NAME = "track_not_car";

        public static final String DATE = "date";
        public static final String INCAR = "incar";

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
                {_ID, DATE, INCAR};

        public static final String[] UNIQUE_COLUMNS =
                {DATE};

        public static final String DEFAULT_SORT_ORDER = DATE + " ASC";

    }

    public static final class FuelContract implements BaseColumns{

        public static final String TABLE_NAME = "fuel_list";

        public static final String EXTERNAL_ID = "external_id";
        public static final String DELETED = "deleted";
        public static final String INDB = "incdb";
        public static final String DATE = "date_start";
        public static final String TYPE_FUEL = "tupe_fuel";
        public static final String TYPE_PAYMENT = "type_payment";
        public static final String LITRES = "litres";
        public static final String MONEY = "money";
        public static final String POINT_CREATE = "point_create";

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
                {_ID, EXTERNAL_ID, DELETED, INDB, DATE,
                        TYPE_FUEL, TYPE_PAYMENT, LITRES, MONEY, POINT_CREATE};

        public static final String[] UNIQUE_COLUMNS =
                {EXTERNAL_ID};

        public static final String DEFAULT_SORT_ORDER = DATE + " ASC";


    }

}
