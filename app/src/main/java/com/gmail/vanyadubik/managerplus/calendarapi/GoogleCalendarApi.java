package com.gmail.vanyadubik.managerplus.calendarapi;

import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;

import java.util.Date;


public interface GoogleCalendarApi {

    long addEvent(Date dateStart, Date dateEnd, String title, String description, Long calendarId);

    long eventSetStatus(int status, long eventID);

    long addAttendees(String name, String email, long eventID);

    long addReminders(long eventID, int timeMinutes, int remindType);

    int upgrateEvent(Date dateStart, Date dateEnd, String title, String description, long eventID, long calendarId);

    int upgrateEventByVisit(Visit_Document visit_document);

    int deleteEvent(long eventID);

    int deleteEventByVisit(Visit_Document visit_document);

    long getCalendarID();

}
