package com.gmail.vanyadubik.managerplus.calendarapi;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.db.MobileManagerContract.VisitEventContract;
import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;
import com.gmail.vanyadubik.managerplus.utils.SharedStorage;

import java.util.Date;

import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_CONNECTED_VISITS;
import static com.gmail.vanyadubik.managerplus.common.Consts.GOOGLE_ACC_EMAIL;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_G_CALENDAR;

public class CalendarApiImpl implements GoogleCalendarApi {

    private Context mContext;
    private ContentResolver contentResolver;

    public CalendarApiImpl(Context context) {
        this.mContext = context;
        this.contentResolver = context.getContentResolver();
    }

    @Override
    public long addEvent(Date dateStart, Date dateEnd, String title, String description, Long calendarId) {
        long eventID = 0;
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, dateStart.getTime());
        values.put(Events.DTEND, dateEnd.getTime());
        values.put(Events.TITLE, title);
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.EVENT_TIMEZONE, "Ukraine/Kyiv");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = contentResolver.insert(Events.CONTENT_URI, values);
            eventID = Long.parseLong(uri.getLastPathSegment());
            Log.i(TAGLOG_G_CALENDAR, "Event added: " + eventID);
        }
        return eventID;
    }

    @Override
    public long eventSetStatus(int status, long eventID) {
        int rows = 0;
        ContentValues values = new ContentValues();
        values.put(Events.STATUS, status);
        Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            rows = contentResolver.update(updateUri, values, null, null);
            Log.i(TAGLOG_G_CALENDAR, "Rows updated: " + rows);
        }
        return rows;
    }

    @Override
    public long addAttendees(String name, String email, long eventID) {
        long id = 0;
        ContentValues values = new ContentValues();
        values.put(Attendees.ATTENDEE_NAME, name);
        values.put(Attendees.ATTENDEE_EMAIL, email);
        values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);
        values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
        values.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED);
        values.put(Attendees.EVENT_ID, eventID);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = contentResolver.insert(Attendees.CONTENT_URI, values);
            id = Long.parseLong(uri.getLastPathSegment());
            Log.i(TAGLOG_G_CALENDAR, "Attendees added: " + eventID);
        }
        return id;
    }

    @Override
    public long addReminders(long eventID, int timeMinutes, int remindType) {
        long id = 0;
        ContentValues values = new ContentValues();
        values.put(Reminders.MINUTES, timeMinutes);
        values.put(Reminders.EVENT_ID, eventID);
        values.put(Reminders.METHOD, remindType);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = contentResolver.insert(Reminders.CONTENT_URI, values);
            id = Long.parseLong(uri.getLastPathSegment());
            Log.i(TAGLOG_G_CALENDAR, "Reminders added: " + eventID);
        }
        return id;
    }

    @Override
    public int upgrateEvent(Date dateStart, Date dateEnd, String title, String description, long eventID, long calendarId) {
        int rows = 0;
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, dateStart.getTime());
        values.put(Events.DTEND, dateEnd.getTime());
        values.put(Events.TITLE, title);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, calendarId);
        Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            rows = contentResolver.update(updateUri, values, null, null);
            Log.i(TAGLOG_G_CALENDAR, "Rows updated: " + rows);
        }
        return rows;
    }

    @Override
    public int upgrateEventByVisit(Visit_Document visit_document) {
        int rows = 0;

        if(SharedStorage.getBoolean(mContext, GOOGLE_ACC_CONNECTED_VISITS, false)){
            int eventId = getEventIdByVisitId(visit_document.getId());
            long calendarId = getCalendarID();
            long result = 0;
            if(eventId> 0){
                result = upgrateEvent(visit_document.getDateVisit(),
                        visit_document.getDateVisit(),
                        visit_document.getInformation(),
                        visit_document.getInformation(),
                        eventId, calendarId);
                insertVisitEvent(visit_document.getId(), eventId);
            }else{
                result = addEvent(visit_document.getDateVisit(),
                    visit_document.getDateVisit(),
                    visit_document.getInformation(),
                    visit_document.getInformation(),
                    calendarId);
                }

            if(result>0) {
                addReminders(eventId, 15, Reminders.METHOD_ALERT);
            }
        }
        return rows;
    }

    @Override
    public int deletedEvent(long eventID) {

        Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
        int rows = contentResolver.delete(deleteUri, null, null);
        Log.i(TAGLOG_G_CALENDAR, "Rows deleted: " + rows);

        return rows;

    }

    @Override
    public long getCalendarID() {

        long calID = 0;

        String[] EVENT_PROJECTION = new String[] {
                Calendars._ID,
                Calendars.ACCOUNT_NAME,
                Calendars.CALENDAR_DISPLAY_NAME,
                Calendars.OWNER_ACCOUNT
        };

        int PROJECTION_ID_INDEX = 0;
        int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        int PROJECTION_DISPLAY_NAME_INDEX = 2;
        int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        Uri uri = Calendars.CONTENT_URI;
        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {SharedStorage.getString(mContext, GOOGLE_ACC_EMAIL, ""), "com.google",
                SharedStorage.getString(mContext, GOOGLE_ACC_EMAIL, "")};

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor cur = contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

            while (cur.moveToNext()) {
                calID = cur.getLong(PROJECTION_ID_INDEX);
                String displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                String accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                String ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            }
        }


        return calID;
    }

    public int getVisitIdbyEventId(int eventId) {
        int visitId = 0;
        try (Cursor cursor = contentResolver.query(
                VisitEventContract.CONTENT_URI,
                VisitEventContract.PROJECTION_ALL,
                VisitEventContract.EVENT_ID + "='" + eventId + "'",
                new String[]{},
                VisitEventContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex("count()")) == 0) {
                return 0;
            }
            return cursor.getInt(cursor.getColumnIndex(VisitEventContract.VISIT_ID));
        }

    }

    public int getEventIdByVisitId(int visitId) {
        try (Cursor cursor = contentResolver.query(
                VisitEventContract.CONTENT_URI,
                VisitEventContract.PROJECTION_ALL,
                VisitEventContract.VISIT_ID + "='" + visitId + "'",
                new String[]{},
                VisitEventContract.DEFAULT_SORT_ORDER)) {

            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(cursor.getColumnIndex("count()")) == 0) {
                return 0;
            }
            return cursor.getInt(cursor.getColumnIndex(VisitEventContract.EVENT_ID));
        }
    }

    public void insertVisitEvent(int visitId, int eventId) {
        ContentValues values = new ContentValues();
        values.put(VisitEventContract.VISIT_ID, visitId);
        values.put(VisitEventContract.EVENT_ID, eventId);

        contentResolver.insert(VisitEventContract.CONTENT_URI, values);
    }
}