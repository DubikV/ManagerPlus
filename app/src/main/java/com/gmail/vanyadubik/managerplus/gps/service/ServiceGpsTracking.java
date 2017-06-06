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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.stringPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.booleanPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.integerPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.serviceStringPrefs;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;

public class ServiceGpsTracking extends Service {
    static final String RECEIVER_FILTER = "gps.service.serviceGpsTracking.serviceBroadcaster";
    final int CHECK_PREFS;
    final String LINE_ARGS;
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

    class TickTimer implements Runnable {
        TickTimer() {
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
            timeStamp.setTimeInMillis(getCurrentTime());
            int time = (timeStamp.get(11) * 60) + timeStamp.get(12);
            int day = timeStamp.get(7);
            if (_sendTimerInProgress) {
                SystemClock.sleep(300);
            }
            _tickTimerInProgress = true;
            if (_startTime != _endTime) {
                if (time + ((_interval / 1000) / 60) > (time < _startTime ? _startTime : _endTime)) {
                    access$2100 = (((time < _startTime ? _startTime : _endTime) - time) * 60) * 1000;
                    abs = Math.abs(access$2100);
                    if (_startTime != _endTime && (time < _startTime || time >= _endTime)) {
                        Log.i(TAGLOG_GPS, "The current position has not been recorded. The current time is outside the working time limits");
                        isStoped = true;
                        abs = (((time <= _endTime ? ((1440 - _endTime) + _startTime) - (time - _endTime) : _startTime - time) * 60) * 1000) - (timeStamp.get(13) * 1000);
                    }
                    if ((((int) Math.pow(2.0d, (double) (day + -2 >= 0 ? day + 5 : day - 2))) & _days) == 0 && !isStoped) {
                        Log.i(TAGLOG_GPS, "The current position has not been recorded. The current day is outside the working days limits");
                        isStoped = true;
                        abs = ((1440 - time) * 60) * 1000;
                        day = day >= 7 ? day + 1 : 1;
                        while (abs < 604800000) {
                            if ((((int) Math.pow(2.0d, (double) (day + -2 >= 0 ? day + 5 : day - 2))) & _days) == 0) {
                                break;
                            }
                            abs += 86400000;
                            if (day >= 7) {
                                day++;
                            } else {
                                day = 1;
                            }
                        }
                        abs = (abs + ((_startTime * 60) * 1000)) - (timeStamp.get(13) * 1000);
                    }
                    if (!(_isNotificationEnabled || isStoped)) {
                        if (_interval != 0 || _period == 0 || _serverAddress.equals(BuildConfig.VERSION_NAME)) {
                            access$2700 = getNotification(false);
                        } else {
                            access$2700 = getNotification(true);
                        }
                        startForeground(_notifyId, access$2700.build());
                        updateProvider(true);
                    }
                    if (_isNotificationEnabled && isStoped) {
                        stopForeground(false);
                        _notificationManager.cancel(_notifyId);
                        _isNotificationEnabled = false;
                        updateProvider(false);
                    }
                    boolean isGPSEnabled = ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
                    if (!((_gpsLatitude != 0.0 && _gpsLongitude != 0.0) || isStoped || _bSendNull)) {
                        Log.i(TAGLOG_GPS, "The current position is not recorded, the coordinates received incorrectly");
                        isStoped = true;
                    }
//                    if (!(file.exists() || isStoped || file.createNewFile())) {
//                        Log.e(TAGLOG_GPS, "An error occurred while creating the file in ServiceGpsTracking method");
//                        isStoped = true;
//                    }
                    if (!isStoped) {
//                        PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
                        latitude = Gps.CorrectGPSDegree(_gpsLatitude);
                        longitude = Gps.CorrectGPSDegree(_gpsLongitude);
                        if (!isGPSEnabled && _bSendNull) {
                            latitude = -100.0d;
                            longitude = -100.0d;
                        }
                        saveLastLocation(timeStamp, latitude, longitude, _gpsSpeed, _gpsLocationSource);
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
                    _tickTimerInProgress = false;
                    if (abs < 1000) {
                        abs = _period;
                    }
                    tickHandler.postDelayed(tickTimer, (long) abs);
                }
            }
            access$2100 = _interval;
            abs = Math.abs(access$2100);
//            try {
                Log.i(TAGLOG_GPS, "The current position has not been recorded. The current time is outside the working time limits");
                isStoped = true;
                if (time <= _endTime) {
                }
                abs = (((time <= _endTime ? ((1440 - _endTime) + _startTime) - (time - _endTime) : _startTime - time) * 60) * 1000) - (timeStamp.get(13) * 1000);
                if (day + -2 >= 0) {
                }
                Log.i(TAGLOG_GPS, "The current position has not been recorded. The current day is outside the working days limits");
                isStoped = true;
                abs = ((1440 - time) * 60) * 1000;
                if (day >= 7) {
                }
                while (abs < 604800000) {
                    if (day + -2 >= 0) {
                    }
                    if ((((int) Math.pow(2.0d, (double) (day + -2 >= 0 ? day + 5 : day - 2))) & _days) == 0) {
                        break;
//                        abs = (abs + ((_startTime * 60) * 1000)) - (timeStamp.get(13) * 1000);
//                        if (_interval != 0) {
//                        }
//                        access$2700 = getNotification(false);
//                        startForeground(_notifyId, access$2700.build());
//                        updateProvider(true);
//                        stopForeground(false);
//                        _notificationManager.cancel(_notifyId);
//                        _isNotificationEnabled = false;
//                        updateProvider(false);
//                        boolean isGPSEnabled2 = ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
//                        Log.i(TAGLOG_GPS, "The current position is not recorded, the coordinates received incorrectly");
//                        isStoped = true;
//                        Log.e(TAGLOG_GPS, "An error occurred while creating the file in ServiceGpsTracking method");
//                        isStoped = true;
//                        if (isStoped) {
////                            PrintWriter printWriter2 = new PrintWriter(new FileOutputStream(file, true));
//                            latitude = Gps.CorrectGPSDegree(_gpsLatitude);
//                            longitude = Gps.CorrectGPSDegree(_gpsLongitude);
//                            latitude = -100.0;
//                            longitude = -100.0;
//                            saveLastLocation(timeStamp, latitude, longitude, _gpsSpeed, _gpsLocationSource);
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
//                        _tickTimerInProgress = false;
//                        if (abs < 1000) {
//                            abs = _period;
//                        }
//                        tickHandler.postDelayed(tickTimer, (long) abs);
                    }
                    abs += 86400000;
                    if (day >= 7) {
                        day = 1;
                    } else {
                        day++;
                    }
                }
                abs = (abs + ((_startTime * 60) * 1000)) - (timeStamp.get(13) * 1000);
                if (_interval != 0) {
                }
                access$2700 = getNotification(false);
                startForeground(_notifyId, access$2700.build());
                updateProvider(true);
                stopForeground(false);
                _notificationManager.cancel(_notifyId);
                _isNotificationEnabled = false;
                updateProvider(false);
                boolean isGPSEnabled22 = ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS);
                Log.i(TAGLOG_GPS, "The current position is not recorded, the coordinates received incorrectly");
                isStoped = true;
                Log.e(TAGLOG_GPS, "An error occurred while creating the file in ServiceGpsTracking method");
                isStoped = true;
                if (isStoped) {
//                    PrintWriter printWriter22 = new PrintWriter(new FileOutputStream(file, true));
                    latitude = Gps.CorrectGPSDegree(_gpsLatitude);
                    longitude = Gps.CorrectGPSDegree(_gpsLongitude);
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
            _tickTimerInProgress = false;
            if (abs < 1000) {
                abs = _period;
            }
            tickHandler.postDelayed(tickTimer, (long) abs);
        }
    }

    private class GpsTrackingLocationListener implements LocationListener {
        private GpsTrackingLocationListener() {
        }

        private void onGpsStatusChanged(int status) {
            if (status != 2) {
                _gpsLatitude = 0.0d;
                _gpsLongitude = 0.0d;
            }
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                _gpsLatitude = location.getLatitude();
                _gpsLongitude = location.getLongitude();
                _gpsSpeed = (double) location.getSpeed();
                _gpsLocationSource = Provider.FromName(location.getProvider()).getIndex();
                if (_gpsStatus != 2) {
                    _gpsStatus = 2;
                    onGpsStatusChanged(_gpsStatus);
                }
            }
        }

        public void onProviderDisabled(String provider) {
            _gpsStatus = 0;
            onGpsStatusChanged(_gpsStatus);
            renewNotification(1);
        }

        public void onProviderEnabled(String provider) {
            _gpsStatus = 2;
            onGpsStatusChanged(_gpsStatus);
            renewNotification(1);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public ServiceGpsTracking() {
        _interval = 0;
        _period = 0;
        _gpsStatus = 0;
        _isTickTimerStarted = false;
        _isSendTimerStarted = false;
        _sendTimerInProgress = false;
        _tickTimerInProgress = false;
        _context = this;
        sendHandler = new Handler();
        tickHandler = new Handler();
        LINE_ARGS = "%04d-%02d-%02d %02d-%02d-%02d\t%9.4f\t%9.4f";
        SPEED_ARGS = "\t%.2f";
        SOURCE_ARGS = "\t%d";
 //       NEW_LINE = SocketClient.NETASCII_EOL;
        RENEW_PREFS = 0;
        CHECK_PREFS = 1;
        SET_PREFS = 2;
        tickTimer = new TickTimer();
    }

    private void startTickTimer(boolean isActivate) {
        if (isActivate && !_isTickTimerStarted) {
            _isTickTimerStarted = true;
            tickHandler.post(tickTimer);
        } else if (!isActivate && _isTickTimerStarted) {
            _isTickTimerStarted = false;
            tickHandler.removeCallbacks(tickTimer);
        }
    }

    private void startSendingTimer(boolean isActivate) {
        if (isActivate && !_isSendTimerStarted) {
            _isSendTimerStarted = true;
            while (_period % _interval == 0) {
                _period += 900;
            }
            if (_period > 0) {
                sendHandler.postDelayed(sendTimer, (long) _period);
            }
        } else if (!isActivate && _isSendTimerStarted) {
            _isSendTimerStarted = false;
            sendHandler.removeCallbacks(sendTimer);
        }
    }

    private void saveLastLocation(Calendar dt, double latitude, double longitude, double speed, int locationSource) {
        Intent intent = new Intent("gps.service.GpsTracking.initializerBroadcaster");
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
        _isNotificationEnabled = true;
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
        int time = (timeStamp.get(11) * 60) + timeStamp.get(12);
        int day = timeStamp.get(7);
        if (_interval != 0 && _period != 0 && !_serverAddress.equals("") && ((LocationManager)getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS)) {
            Label_0270: {
                if ((isInitialize == 0 || isInitialize == 1) && (_startTime == _endTime || (time >= _startTime && time < _endTime))) {
                    final int days = _days;
                    int n3;
                    if (day - 2 < 0) {
                        n3 = day + 5;
                    }
                    else {
                        n3 = day - 2;
                    }
                    if (((int)Math.pow(2.0, n3) & days) != 0x0) {
                        break Label_0270;
                    }
                }
                if (isInitialize != 2) {
                    return;
                }
            }
            _notificationManager.cancel(_notifyId);
            _isNotificationEnabled = false;
            _notificationManager.notify(_notifyId, getNotification(true).build());
            return;
        }
        Label_0157: {
            if ((isInitialize == 0 || isInitialize == 1) && (_startTime == _endTime || (time >= _startTime && time < _endTime))) {
                final int days2 = _days;
                int n4;
                if (day - 2 < 0) {
                    n4 = day + 5;
                }
                else {
                    n4 = day - 2;
                }
                if (((int)Math.pow(2.0, n4) & days2) != 0x0) {
                    break Label_0157;
                }
            }
            if (isInitialize != 2) {
                return;
            }
        }
        _notificationManager.cancel(_notifyId);
        _isNotificationEnabled = false;
        _notificationManager.notify(_notifyId, getNotification(false).build());
    }

    private void setNewPreference(int isInitialize, int[] integerParams, boolean[] booleanParams, ArrayList<String> stringParams) {
        if (isInitialize == 0 || isInitialize == 2) {
            _startTime = integerParams[integerPrefs.TIME.getID()] >> 16;
            _endTime = integerParams[integerPrefs.TIME.getID()] & SupportMenu.USER_MASK;
            _interval = integerParams[integerPrefs.INTERVAL.getID()] * 1000;
            _days = integerParams[integerPrefs.DAYS.getID()];
            _period = integerParams[integerPrefs.PERIOD.getID()] * 1000;
            _port = integerParams[integerPrefs.PORT.getID()];
            _serverType = integerParams[integerPrefs.SERVER_TYPE.getID()];
            _locationSource = integerParams[integerPrefs.INDEX_LOCATION_SOURCE.getID()];
            _bSpeed = booleanParams[booleanPrefs.SPEED.getID()];
            _bGpsTime = booleanParams[booleanPrefs.GPS_TIME.getID()];
            _bLocationSource = booleanParams[booleanPrefs.LOCATION_SOURCE.getID()];
            _bSendNull = booleanParams[booleanPrefs.SEND_NULL.getID()];
            _bPassiveConnection = booleanParams[booleanPrefs.PASSIVE_CONNECTION.getID()];
            _trackFile = (String) stringParams.get(stringPrefs.FILE.getID());
            _serverAddress = (String) stringParams.get(stringPrefs.SERVER_ADDRESS.getID());
            _ppcGuid = (String) stringParams.get(stringPrefs.PPC_GUID.getID());
            _erpId = (String) stringParams.get(stringPrefs.ERP_ID.getID());
            _username = (String) stringParams.get(stringPrefs.USERNAME.getID());
            _password = (String) stringParams.get(stringPrefs.PASSWORD.getID());
            _filePath = (String) stringParams.get(stringPrefs.FILEPATH.getID());
            _fileName = (String) stringParams.get(stringPrefs.FILENAME.getID());
            if (isInitialize == 2) {
                startForeground(_notifyId, getNotification(true).build());
                if (VERSION.SDK_INT > 22) {
                    stopForeground(false);
                }
                startTickTimer(true);
                if (_period > 0) {
                    startSendingTimer(true);
                }
            }
        }
        renewNotification(isInitialize);
    }

    private void setReceiver() {

        serviceReceiver = new BroadcastReceiver(){

            public void onReceive(Context context, Intent intent) {
                String Action = intent.getStringExtra("brc_receiver_action");
                int[] IntegerParams = intent.getIntArrayExtra("numeric_shared_preferences");
                boolean[] BooleanParams = intent.getBooleanArrayExtra("boolean_shared_preferences");
                ArrayList<String> StringParams = intent.getStringArrayListExtra("string_shared_preferences");
                if (Action.equals("initializer_have_new_preferences")) {
                    boolean isTTStartedBefore = _isTickTimerStarted;
                    boolean isSTStartedBefore = _isSendTimerStarted;
                    startTickTimer(false);
                    startSendingTimer(false);
                    setNewPreference(0, IntegerParams, BooleanParams, StringParams);
                    startTickTimer(isTTStartedBefore);
                    startSendingTimer(isSTStartedBefore);
                }
                if (Action.equals("initializer_wait_for_check_preferences")) {
                    setNewPreference(1, IntegerParams, BooleanParams, StringParams);
                }
                if (Action.equals("send_preferences_to_service")) {
                    setNewPreference(2, IntegerParams, BooleanParams, StringParams);
                }
            }
        };

        this.registerReceiver(serviceReceiver, new IntentFilter(RECEIVER_FILTER));
        //registerReceiver(serviceReceiver, new IntentFilter(RECEIVER_FILTER));
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private void updateProvider(boolean isStart) {
        if (isStart) {
            if (_locManager == null) {
                Provider provider = Provider.FromIndex(0);
                _locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                _locListener = new GpsTrackingLocationListener();
                if (provider == Provider.PASSIVE) {
                    List<String> providerList = _locManager.getAllProviders();
                    if ( Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (providerList.contains(Provider.PROVIDER_GPS)) {
                        _locManager.requestLocationUpdates(Provider.GPS.getName(), 0, 0.0f, _locListener);
                    }
                    if (providerList.contains(Provider.PROVIDER_NETWORK)) {
                        _locManager.requestLocationUpdates(Provider.NETWORK.getName(), 0, 0.0f, _locListener);
                        return;
                    }
                    return;
                }
                _locManager.requestLocationUpdates(provider.getName(), 0, 0.0f, _locListener);
            }
        } else if (_locManager != null) {
            _locManager.removeUpdates(_locListener);
            _locManager = null;
        }
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!_isTickTimerStarted) {
            _notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            _notifyId = startId;
            setReceiver();
            updateProvider(true);
        }
        return Service.START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
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