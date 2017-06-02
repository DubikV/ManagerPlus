package com.gmail.vanyadubik.managerplus.gps.service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.stringPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.booleanPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.integerPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.serviceStringPrefs;

import static android.R.attr.x;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;
import static com.google.android.gms.internal.zznu.is;

public class ServiceGpsTracking extends Service {
    final int CHECK_PREFS;
    final String LINE_ARGS;
 //   final String NEW_LINE;
    final int RENEW_PREFS;
    final int SET_PREFS;
    final String SOURCE_ARGS;
    final String SPEED_ARGS;
    private boolean _bGpsTime;
    private boolean _bLocationSource;
    private boolean _bPassiveConnection;
    private boolean _bSendNull;
    private boolean _bSpeed;
    private Context _context;
    private int _days;
    private int _endTime;
    private String _erpId;
    private String _fileName;
    private String _filePath;
    private double _gpsLatitude;
    private int _gpsLocationSource;
    private double _gpsLongitude;
    private double _gpsSpeed;
    private int _gpsStatus;
    private int _interval;
    private boolean _isNotificationEnabled;
    private boolean _isSendTimerStarted;
    private boolean _isTickTimerStarted;
    private LocationListener _locListener;
    private LocationManager _locManager;
    private int _locationSource;
    private NotificationManager _notificationManager;
    private int _notifyId;
    private String _password;
    private int _period;
    private int _port;
    private String _ppcGuid;
    private boolean _sendTimerInProgress;
    private String _serverAddress;
    private int _serverType;
    private int _startTime;
    private boolean _tickTimerInProgress;
    private String _trackFile;
    private String _username;
    private Handler sendHandler;
    private Runnable sendTimer;
    private BroadcastReceiver serviceReceiver;
    private Handler tickHandler;
    private Runnable tickTimer;

    class C04741 implements Runnable {
        C04741() {
        }

        public void run() {
            int access$2100;
            int abs;
            Builder access$2700;
            double latitude;
            double longitude;
            String locationLine;
            Object[] objArr;
            boolean isStoped = false;
            Calendar timeStamp = Calendar.getInstance();
            timeStamp.setTimeInMillis(ServiceGpsTracking.this.getCurrentTime());
            int time = (timeStamp.get(11) * 60) + timeStamp.get(12);
            int day = timeStamp.get(7);
            if (ServiceGpsTracking.this._sendTimerInProgress) {
                SystemClock.sleep(300);
            }
            ServiceGpsTracking.this._tickTimerInProgress = true;
            if (ServiceGpsTracking.this._startTime != ServiceGpsTracking.this._endTime) {
                if (time + ((ServiceGpsTracking.this._interval / 1000) / 60) > (time < ServiceGpsTracking.this._startTime ? ServiceGpsTracking.this._startTime : ServiceGpsTracking.this._endTime)) {
                    access$2100 = (((time < ServiceGpsTracking.this._startTime ? ServiceGpsTracking.this._startTime : ServiceGpsTracking.this._endTime) - time) * 60) * 1000;
                    abs = Math.abs(access$2100);
                    if (ServiceGpsTracking.this._startTime != ServiceGpsTracking.this._endTime && (time < ServiceGpsTracking.this._startTime || time >= ServiceGpsTracking.this._endTime)) {
                        Log.i(TAGLOG_GPS, "The current position has not been recorded. The current time is outside the working time limits");
                        isStoped = true;
                        abs = (((time <= ServiceGpsTracking.this._endTime ? ((1440 - ServiceGpsTracking.this._endTime) + ServiceGpsTracking.this._startTime) - (time - ServiceGpsTracking.this._endTime) : ServiceGpsTracking.this._startTime - time) * 60) * 1000) - (timeStamp.get(13) * 1000);
                    }
                    if ((((int) Math.pow(2.0d, (double) (day + -2 >= 0 ? day + 5 : day - 2))) & ServiceGpsTracking.this._days) == 0 && !isStoped) {
                        Log.i(TAGLOG_GPS, "The current position has not been recorded. The current day is outside the working days limits");
                        isStoped = true;
                        abs = ((1440 - time) * 60) * 1000;
                        day = day >= 7 ? day + 1 : 1;
                        while (abs < 604800000) {
                            if ((((int) Math.pow(2.0d, (double) (day + -2 >= 0 ? day + 5 : day - 2))) & ServiceGpsTracking.this._days) == 0) {
                                break;
                            }
                            abs += 86400000;
                            if (day >= 7) {
                                day++;
                            } else {
                                day = 1;
                            }
                        }
                        abs = (abs + ((ServiceGpsTracking.this._startTime * 60) * 1000)) - (timeStamp.get(13) * 1000);
                    }
                    if (!(ServiceGpsTracking.this._isNotificationEnabled || isStoped)) {
                        if (ServiceGpsTracking.this._interval != 0 || ServiceGpsTracking.this._period == 0 || ServiceGpsTracking.this._serverAddress.equals(BuildConfig.VERSION_NAME)) {
                            access$2700 = ServiceGpsTracking.this.getNotification(false);
                        } else {
                            access$2700 = ServiceGpsTracking.this.getNotification(true);
                        }
                        ServiceGpsTracking.this.startForeground(ServiceGpsTracking.this._notifyId, access$2700.build());
                        ServiceGpsTracking.this.updateProvider(true);
                    }
                    if (ServiceGpsTracking.this._isNotificationEnabled && isStoped) {
                        ServiceGpsTracking.this.stopForeground(false);
                        ServiceGpsTracking.this._notificationManager.cancel(ServiceGpsTracking.this._notifyId);
                        ServiceGpsTracking.this._isNotificationEnabled = false;
                        ServiceGpsTracking.this.updateProvider(false);
                    }
                    boolean isGPSEnabled = ((LocationManager) ServiceGpsTracking.this.getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
                    if (!((ServiceGpsTracking.this._gpsLatitude != 0.0d && ServiceGpsTracking.this._gpsLongitude != 0.0d) || isStoped || ServiceGpsTracking.this._bSendNull)) {
                        Log.i(TAGLOG_GPS, "The current position is not recorded, the coordinates received incorrectly");
                        isStoped = true;
                    }
//                    if (!(file.exists() || isStoped || file.createNewFile())) {
//                        Log.e(TAGLOG_GPS, "An error occurred while creating the file in ServiceGpsTracking method");
//                        isStoped = true;
//                    }
                    if (!isStoped) {
//                        PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
                        latitude = Gps.CorrectGPSDegree(ServiceGpsTracking.this._gpsLatitude);
                        longitude = Gps.CorrectGPSDegree(ServiceGpsTracking.this._gpsLongitude);
                        if (!isGPSEnabled && ServiceGpsTracking.this._bSendNull) {
                            latitude = -100.0d;
                            longitude = -100.0d;
                        }
                        ServiceGpsTracking.this.saveLastLocation(timeStamp, latitude, longitude, ServiceGpsTracking.this._gpsSpeed, ServiceGpsTracking.this._gpsLocationSource);
//                        locationLine = String.format(Locale.US, "%04d-%02d-%02d %02d-%02d-%02d\t%9.4f\t%9.4f", new Object[]{Integer.valueOf(timeStamp.get(1)), Integer.valueOf(timeStamp.get(2) + 1), Integer.valueOf(timeStamp.get(5)), Integer.valueOf(timeStamp.get(11)), Integer.valueOf(timeStamp.get(12)), Integer.valueOf(timeStamp.get(13)), Double.valueOf(latitude), Double.valueOf(longitude)});
//                        if (ServiceGpsTracking.this._bSpeed) {
//                            StringBuilder append = new StringBuilder().append(locationLine);
//                            objArr = new Object[1];
//                            objArr[0] = Double.valueOf(ServiceGpsTracking.this._gpsSpeed);
//                            locationLine = append.append(String.format(Locale.US, "\t%.2f", objArr)).toString();
//                        }
//                        if (ServiceGpsTracking.this._bLocationSource) {
//                            StringBuilder append2 = new StringBuilder().append(locationLine);
//                            Locale locale = Locale.US;
//                            String str = (ServiceGpsTracking.this._bSpeed ? BuildConfig.VERSION_NAME : "\t") + "\t%d";
//                            objArr = new Object[1];
//                            objArr[0] = Integer.valueOf(ServiceGpsTracking.this._gpsLocationSource);
//                            locationLine = append2.append(String.format(locale, str, objArr)).toString();
//                        }
//                        printWriter.print(locationLine + SocketClient.NETASCII_EOL);
//                        printWriter.close();
                    }
                    ServiceGpsTracking.this._tickTimerInProgress = false;
                    if (abs < 1000) {
                        abs = ServiceGpsTracking.this._period;
                    }
                    ServiceGpsTracking.this.tickHandler.postDelayed(ServiceGpsTracking.this.tickTimer, (long) abs);
                }
            }
            access$2100 = ServiceGpsTracking.this._interval;
            abs = Math.abs(access$2100);
//            try {
                Log.i(TAGLOG_GPS, "The current position has not been recorded. The current time is outside the working time limits");
                isStoped = true;
                if (time <= ServiceGpsTracking.this._endTime) {
                }
                abs = (((time <= ServiceGpsTracking.this._endTime ? ((1440 - ServiceGpsTracking.this._endTime) + ServiceGpsTracking.this._startTime) - (time - ServiceGpsTracking.this._endTime) : ServiceGpsTracking.this._startTime - time) * 60) * 1000) - (timeStamp.get(13) * 1000);
                if (day + -2 >= 0) {
                }
                Log.i(TAGLOG_GPS, "The current position has not been recorded. The current day is outside the working days limits");
                isStoped = true;
                abs = ((1440 - time) * 60) * 1000;
                if (day >= 7) {
                }
//                while (abs < 604800000) {
//                    if (day + -2 >= 0) {
//                    }
//                    if ((((int) Math.pow(2.0d, (double) (day + -2 >= 0 ? day + 5 : day - 2))) & ServiceGpsTracking.this._days) == 0) {
//                        break;
//                        abs = (abs + ((ServiceGpsTracking.this._startTime * 60) * 1000)) - (timeStamp.get(13) * 1000);
//                        if (ServiceGpsTracking.this._interval != 0) {
//                        }
//                        access$2700 = ServiceGpsTracking.this.getNotification(false);
//                        ServiceGpsTracking.this.startForeground(ServiceGpsTracking.this._notifyId, access$2700.build());
//                        ServiceGpsTracking.this.updateProvider(true);
//                        ServiceGpsTracking.this.stopForeground(false);
//                        ServiceGpsTracking.this._notificationManager.cancel(ServiceGpsTracking.this._notifyId);
//                        ServiceGpsTracking.this._isNotificationEnabled = false;
//                        ServiceGpsTracking.this.updateProvider(false);
//                        boolean isGPSEnabled2 = ((LocationManager) ServiceGpsTracking.this.getSystemService("location")).isProviderEnabled(Provider.PROVIDER_GPS);
//                        Log.i(TAGLOG_GPS, "The current position is not recorded, the coordinates received incorrectly");
//                        isStoped = true;
//                        Log.e(TAGLOG_GPS, "An error occurred while creating the file in ServiceGpsTracking method");
//                        isStoped = true;
//                        if (isStoped) {
////                            PrintWriter printWriter2 = new PrintWriter(new FileOutputStream(file, true));
//                            latitude = Gps.CorrectGPSDegree(ServiceGpsTracking.this._gpsLatitude);
//                            longitude = Gps.CorrectGPSDegree(ServiceGpsTracking.this._gpsLongitude);
//                            latitude = -100.0d;
//                            longitude = -100.0d;
//                            ServiceGpsTracking.this.saveLastLocation(timeStamp, latitude, longitude, ServiceGpsTracking.this._gpsSpeed, ServiceGpsTracking.this._gpsLocationSource);
////                            locationLine = String.format(Locale.US, "%04d-%02d-%02d %02d-%02d-%02d\t%9.4f\t%9.4f", new Object[]{Integer.valueOf(timeStamp.get(1)), Integer.valueOf(timeStamp.get(2) + 1), Integer.valueOf(timeStamp.get(5)), Integer.valueOf(timeStamp.get(11)), Integer.valueOf(timeStamp.get(12)), Integer.valueOf(timeStamp.get(13)), Double.valueOf(latitude), Double.valueOf(longitude)});
////                            if (ServiceGpsTracking.this._bSpeed) {
////                                StringBuilder append3 = new StringBuilder().append(locationLine);
////                                objArr = new Object[1];
////                                objArr[0] = Double.valueOf(ServiceGpsTracking.this._gpsSpeed);
////                                locationLine = append3.append(String.format(Locale.US, "\t%.2f", objArr)).toString();
////                            }
////                            if (ServiceGpsTracking.this._bLocationSource) {
////                                StringBuilder append22 = new StringBuilder().append(locationLine);
////                                Locale locale2 = Locale.US;
////                                if (ServiceGpsTracking.this._bSpeed) {
////                                }
////                                String str2 = (ServiceGpsTracking.this._bSpeed ? BuildConfig.VERSION_NAME : "\t") + "\t%d";
////                                objArr = new Object[1];
////                                objArr[0] = Integer.valueOf(ServiceGpsTracking.this._gpsLocationSource);
////                                locationLine = append22.append(String.format(locale2, str2, objArr)).toString();
////                            }
////                            printWriter2.print(locationLine + SocketClient.NETASCII_EOL);
////                            printWriter2.close();
//                        }
//                        ServiceGpsTracking.this._tickTimerInProgress = false;
//                        if (abs < 1000) {
//                            abs = ServiceGpsTracking.this._period;
//                        }
//                        ServiceGpsTracking.this.tickHandler.postDelayed(ServiceGpsTracking.this.tickTimer, (long) abs);
//                    }
//                    abs += 86400000;
//                    if (day >= 7) {
//                        day = 1;
//                    } else {
//                        day++;
//                    }
//                }
                abs = (abs + ((ServiceGpsTracking.this._startTime * 60) * 1000)) - (timeStamp.get(13) * 1000);
                if (ServiceGpsTracking.this._interval != 0) {
                }
                access$2700 = ServiceGpsTracking.this.getNotification(false);
                ServiceGpsTracking.this.startForeground(ServiceGpsTracking.this._notifyId, access$2700.build());
                ServiceGpsTracking.this.updateProvider(true);
                ServiceGpsTracking.this.stopForeground(false);
                ServiceGpsTracking.this._notificationManager.cancel(ServiceGpsTracking.this._notifyId);
                ServiceGpsTracking.this._isNotificationEnabled = false;
                ServiceGpsTracking.this.updateProvider(false);
                boolean isGPSEnabled22 = ((LocationManager) ServiceGpsTracking.this.getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
                Log.i("agentp2_gpstracking", "The current position is not recorded, the coordinates received incorrectly");
                isStoped = true;
                Log.e("agentp2_gpstracking", "An error occurred while creating the file in ServiceGpsTracking method");
                isStoped = true;
                if (isStoped) {
//                    PrintWriter printWriter22 = new PrintWriter(new FileOutputStream(file, true));
                    latitude = Gps.CorrectGPSDegree(ServiceGpsTracking.this._gpsLatitude);
                    longitude = Gps.CorrectGPSDegree(ServiceGpsTracking.this._gpsLongitude);
                    latitude = -100.0d;
                    longitude = -100.0d;
//                    ServiceGpsTracking.this.saveLastLocation(timeStamp, latitude, longitude, ServiceGpsTracking.this._gpsSpeed, ServiceGpsTracking.this._gpsLocationSource);
//                    locationLine = String.format(Locale.US, "%04d-%02d-%02d %02d-%02d-%02d\t%9.4f\t%9.4f", new Object[]{Integer.valueOf(timeStamp.get(1)), Integer.valueOf(timeStamp.get(2) + 1), Integer.valueOf(timeStamp.get(5)), Integer.valueOf(timeStamp.get(11)), Integer.valueOf(timeStamp.get(12)), Integer.valueOf(timeStamp.get(13)), Double.valueOf(latitude), Double.valueOf(longitude)});
//                    if (ServiceGpsTracking.this._bSpeed) {
//                        StringBuilder append32 = new StringBuilder().append(locationLine);
//                        objArr = new Object[1];
//                        objArr[0] = Double.valueOf(ServiceGpsTracking.this._gpsSpeed);
//                        locationLine = append32.append(String.format(Locale.US, "\t%.2f", objArr)).toString();
//                    }
//                    if (ServiceGpsTracking.this._bLocationSource) {
//                        StringBuilder append222 = new StringBuilder().append(locationLine);
//                        Locale locale22 = Locale.US;
//                        if (ServiceGpsTracking.this._bSpeed) {
//                        }
//                        String str22 = (ServiceGpsTracking.this._bSpeed ? BuildConfig.VERSION_NAME : "\t") + "\t%d";
//                        objArr = new Object[1];
//                        objArr[0] = Integer.valueOf(ServiceGpsTracking.this._gpsLocationSource);
//                        locationLine = append222.append(String.format(locale22, str22, objArr)).toString();
//                    }
//                    printWriter22.print(locationLine + SocketClient.NETASCII_EOL);
//                    printWriter22.close();
                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            ServiceGpsTracking.this._tickTimerInProgress = false;
            if (abs < 1000) {
                abs = ServiceGpsTracking.this._period;
            }
            ServiceGpsTracking.this.tickHandler.postDelayed(ServiceGpsTracking.this.tickTimer, (long) abs);
        }
    }

    class C04763 extends BroadcastReceiver {
        C04763() {
        }

        public void onReceive(Context context, Intent intent) {
            String Action = intent.getStringExtra("brc_receiver_action");
            int[] IntegerParams = intent.getIntArrayExtra("numeric_shared_preferences");
            boolean[] BooleanParams = intent.getBooleanArrayExtra("boolean_shared_preferences");
            ArrayList<String> StringParams = intent.getStringArrayListExtra("string_shared_preferences");
            if (Action.equals("initializer_have_new_preferences")) {
                boolean isTTStartedBefore = ServiceGpsTracking.this._isTickTimerStarted;
                boolean isSTStartedBefore = ServiceGpsTracking.this._isSendTimerStarted;
                ServiceGpsTracking.this.startTickTimer(false);
                ServiceGpsTracking.this.startSendingTimer(false);
                ServiceGpsTracking.this.setNewPreference(0, IntegerParams, BooleanParams, StringParams);
                ServiceGpsTracking.this.startTickTimer(isTTStartedBefore);
                ServiceGpsTracking.this.startSendingTimer(isSTStartedBefore);
            }
            if (Action.equals("initializer_wait_for_check_preferences")) {
                ServiceGpsTracking.this.setNewPreference(1, IntegerParams, BooleanParams, StringParams);
            }
            if (Action.equals("send_preferences_to_service")) {
                ServiceGpsTracking.this.setNewPreference(2, IntegerParams, BooleanParams, StringParams);
            }
        }
    }

    private class GpsTrackingLocationListener implements LocationListener {
        private GpsTrackingLocationListener() {
        }

        private void onGpsStatusChanged(int status) {
            if (status != 2) {
                ServiceGpsTracking.this._gpsLatitude = 0.0d;
                ServiceGpsTracking.this._gpsLongitude = 0.0d;
            }
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ServiceGpsTracking.this._gpsLatitude = location.getLatitude();
                ServiceGpsTracking.this._gpsLongitude = location.getLongitude();
                ServiceGpsTracking.this._gpsSpeed = (double) location.getSpeed();
                ServiceGpsTracking.this._gpsLocationSource = Provider.FromName(location.getProvider()).getIndex();
                if (ServiceGpsTracking.this._gpsStatus != 2) {
                    ServiceGpsTracking.this._gpsStatus = 2;
                    onGpsStatusChanged(ServiceGpsTracking.this._gpsStatus);
                }
            }
        }

        public void onProviderDisabled(String provider) {
            ServiceGpsTracking.this._gpsStatus = 0;
            onGpsStatusChanged(ServiceGpsTracking.this._gpsStatus);
            ServiceGpsTracking.this.renewNotification(1);
        }

        public void onProviderEnabled(String provider) {
            ServiceGpsTracking.this._gpsStatus = 2;
            onGpsStatusChanged(ServiceGpsTracking.this._gpsStatus);
            ServiceGpsTracking.this.renewNotification(1);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public ServiceGpsTracking() {
        this._interval = 0;
        this._period = 0;
        this._gpsStatus = 0;
        this._isTickTimerStarted = false;
        this._isSendTimerStarted = false;
        this._sendTimerInProgress = false;
        this._tickTimerInProgress = false;
        this._context = this;
        this.sendHandler = new Handler();
        this.tickHandler = new Handler();
        this.LINE_ARGS = "%04d-%02d-%02d %02d-%02d-%02d\t%9.4f\t%9.4f";
        this.SPEED_ARGS = "\t%.2f";
        this.SOURCE_ARGS = "\t%d";
 //       this.NEW_LINE = SocketClient.NETASCII_EOL;
        this.RENEW_PREFS = 0;
        this.CHECK_PREFS = 1;
        this.SET_PREFS = 2;
        this.tickTimer = new C04741();
    }

    private void startTickTimer(boolean isActivate) {
        if (isActivate && !this._isTickTimerStarted) {
            this._isTickTimerStarted = true;
            this.tickHandler.post(this.tickTimer);
        } else if (!isActivate && this._isTickTimerStarted) {
            this._isTickTimerStarted = false;
            this.tickHandler.removeCallbacks(this.tickTimer);
        }
    }

    private void startSendingTimer(boolean isActivate) {
        if (isActivate && !this._isSendTimerStarted) {
            this._isSendTimerStarted = true;
            while (this._period % this._interval == 0) {
                this._period += 900;
            }
            if (this._period > 0) {
                this.sendHandler.postDelayed(this.sendTimer, (long) this._period);
            }
        } else if (!isActivate && this._isSendTimerStarted) {
            this._isSendTimerStarted = false;
            this.sendHandler.removeCallbacks(this.sendTimer);
        }
    }

    private void saveLastLocation(Calendar dt, double latitude, double longitude, double speed, int locationSource) {
        Intent intent = new Intent("ru.agentp2.GpsTracking.initializerBroadcaster");
        ArrayList<String> stringParams = new ArrayList();
        stringParams.add(serviceStringPrefs.LATITUDE.getID(), Double.toString(latitude));
        stringParams.add(serviceStringPrefs.LONGTITUDE.getID(), Double.toString(longitude));
        stringParams.add(serviceStringPrefs.SPEED.getID(), Double.toString(speed));
        stringParams.add(serviceStringPrefs.LOCATION_SOURCE.getID(), Integer.toString(locationSource));
        intent.putExtra("string_shared_preferences_from_service", stringParams);
        intent.putExtra("long_shared_preferences_from_service", dt.getTimeInMillis());
        sendBroadcast(intent);
    }

    private Builder getNotification(boolean isOk) {
        int i = R.string.service_tracking_message;
        this._isNotificationEnabled = true;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, GpsTracking.class), 0);
        PendingIntent errorPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, GpsTrackingNotification.class).putExtra("fromServiceGpsTrackingNotify", true), 0);
        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(getResources(), isOk ? R.drawable.service_gps_tracking_large : R.drawable.service_gps_tracking_large_error);
        Builder builder = new Builder(this);
        if (!isOk) {
            pendingIntent = errorPendingIntent;
        }
        builder = builder.setContentIntent(pendingIntent).setContentTitle(getString(R.string.app_name) + " |" + getString(R.string.gps_tracer_name)).setContentText(getText(isOk ? R.string.service_tracking_message : R.string.service_tracking_error_message));
        if (!isOk) {
            i = R.string.service_tracking_error_message;
        }
        return builder.setTicker(getString(i)).setSmallIcon(isOk ? R.drawable.service_gps_tracking : R.drawable.service_gps_tracking_error, 1).setLargeIcon(notificationLargeIconBitmap).setColor(getResources().getColor(R.color.colorPrimary)).setOngoing(true).setAutoCancel(true);
    }

    private void renewNotification(int isInitialize) {
        Calendar timeStamp = Calendar.getInstance();
        timeStamp.setTimeInMillis(getCurrentTime());
        int time = (timeStamp.get(11) * 0x3c) + timeStamp.get(12);
        int day = timeStamp.get(7);
        if((_interval == 0) || (_period == 0) || (_serverAddress.equals("")) || (!((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS))) {
            isInitialize == 0x1 ? (_startTime != _endTime) && (time >= _startTime) && (time < _endTime) : (isInitialize == 0x2);
            while((int)Math.pow(0.0) == 0) {
            }
            _notificationManager.cancel(_notifyId);
            _isNotificationEnabled = false;
            _notificationManager.notify(_notifyId, getNotification(false).build());
            return;
        }
        isInitialize == 0x1 ? (_startTime != _endTime) && (time >= _startTime) && (time < _endTime)
                (isInitialize == 0x2)
        while((int)Math.pow(0.0) == 0) {
        }
        _notificationManager.cancel(_notifyId);
        _isNotificationEnabled = false;
        _notificationManager.notify(_notifyId, getNotification(true).build());
    }

    private void setNewPreference(int isInitialize, int[] integerParams, boolean[] booleanParams, ArrayList<String> stringParams) {
        if (isInitialize == 0 || isInitialize == 2) {
            this._startTime = integerParams[integerPrefs.TIME.getID()] >> 16;
            this._endTime = integerParams[integerPrefs.TIME.getID()] & SupportMenu.USER_MASK;
            this._interval = integerParams[integerPrefs.INTERVAL.getID()] * 1000;
            this._days = integerParams[integerPrefs.DAYS.getID()];
            this._period = integerParams[integerPrefs.PERIOD.getID()] * 1000;
            this._port = integerParams[integerPrefs.PORT.getID()];
            this._serverType = integerParams[integerPrefs.SERVER_TYPE.getID()];
            this._locationSource = integerParams[integerPrefs.INDEX_LOCATION_SOURCE.getID()];
            this._bSpeed = booleanParams[booleanPrefs.SPEED.getID()];
            this._bGpsTime = booleanParams[booleanPrefs.GPS_TIME.getID()];
            this._bLocationSource = booleanParams[booleanPrefs.LOCATION_SOURCE.getID()];
            this._bSendNull = booleanParams[booleanPrefs.SEND_NULL.getID()];
            this._bPassiveConnection = booleanParams[booleanPrefs.PASSIVE_CONNECTION.getID()];
            this._trackFile = (String) stringParams.get(stringPrefs.FILE.getID());
            this._serverAddress = (String) stringParams.get(stringPrefs.SERVER_ADDRESS.getID());
            this._ppcGuid = (String) stringParams.get(stringPrefs.PPC_GUID.getID());
            this._erpId = (String) stringParams.get(stringPrefs.ERP_ID.getID());
            this._username = (String) stringParams.get(stringPrefs.USERNAME.getID());
            this._password = (String) stringParams.get(stringPrefs.PASSWORD.getID());
            this._filePath = (String) stringParams.get(stringPrefs.FILEPATH.getID());
            this._fileName = (String) stringParams.get(stringPrefs.FILENAME.getID());
            if (isInitialize == 2) {
                startForeground(this._notifyId, getNotification(true).build());
                if (VERSION.SDK_INT > 22) {
                    stopForeground(false);
                }
                startTickTimer(true);
                if (this._period > 0) {
                    startSendingTimer(true);
                }
            }
        }
        renewNotification(isInitialize);
    }

    private void setReceiver() {
        this.serviceReceiver = new C04763();
        registerReceiver(this.serviceReceiver, new IntentFilter("gps.service.serviceGpsTracking.serviceBroadcaster"));
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private void updateProvider(boolean isStart) {
        if (isStart) {
            if (this._locManager == null) {
                Provider provider = Provider.FromIndex(0);
                this._locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                this._locListener = new GpsTrackingLocationListener();
                if (provider == Provider.PASSIVE) {
                    List<String> providerList = this._locManager.getAllProviders();
                    if ( Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (providerList.contains(Provider.PROVIDER_GPS)) {
                        this._locManager.requestLocationUpdates(Provider.GPS.getName(), 0, 0.0f, this._locListener);
                    }
                    if (providerList.contains(Provider.PROVIDER_NETWORK)) {
                        this._locManager.requestLocationUpdates(Provider.NETWORK.getName(), 0, 0.0f, this._locListener);
                        return;
                    }
                    return;
                }
                this._locManager.requestLocationUpdates(provider.getName(), 0, 0.0f, this._locListener);
            }
        } else if (this._locManager != null) {
            this._locManager.removeUpdates(this._locListener);
            this._locManager = null;
        }
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this._isTickTimerStarted) {
            this._notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
            this._notifyId = startId;
            setReceiver();
            updateProvider(true);
        }
        return Service.START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.serviceReceiver);
        updateProvider(false);
        startSendingTimer(false);
        startTickTimer(false);
    }

    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}