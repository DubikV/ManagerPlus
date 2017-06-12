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
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.integerPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.serviceStringPrefs;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;

public class ServiceGpsTracking extends Service {

    @Inject
    DataRepository dataRepository;

    private Context _context;
    private int _period;
    private int _interval;
    private int _startTime;
    private int _endTime;
    private int _days;
    private double _gpsLatitude;
    private int _gpsLocationSource;
    private double _gpsLongitude;
    private int _gpsStatus;
    private boolean _isNotificationEnabled;
    private boolean _isTickTimerStarted;
    private LocationListener _locListener;
    private LocationManager _locManager;
    private NotificationManager _notificationManager;
    private int _notifyId;
    private BroadcastReceiver serviceReceiver;
    private Handler tickHandler;
    private Runnable tickTimer;

    class TickTimer implements Runnable {
        TickTimer() {
        }

        public void run() {
            int intervalRun, abs;
            Builder notifyBuilder;
            double latitude, longitude;
            boolean isStoped = false;
            Calendar timeStamp = Calendar.getInstance();
            timeStamp.setTimeInMillis(getCurrentTime());
            int time = (timeStamp.get(11) * 60) + timeStamp.get(12);
            int day = timeStamp.get(7);

            abs = (Math.abs(_period) + (timeStamp.get(13) * 1000);

            if (_startTime != _endTime) {
                intervalRun = (((time < _startTime ? _startTime : _endTime) - time) * 60) * 1000;
                abs = Math.abs(intervalRun);

                if (_startTime != _endTime && (time < _startTime || time >= _endTime)) {
                    Log.i(TAGLOG_GPS, "The current position has not been recorded. The current time is outside the working time limits");
                    isStoped = true;
                    abs = (((time <= _endTime ?
                            ((1440 - _endTime) + _startTime) -
                                    (time - _endTime) : _startTime - time) * 60) * 1000) -
                            (timeStamp.get(13) * 1000);

                }
            }

            if ((((int) Math.pow(2.0d, (double) (day - 2 >= 0 ? day + 5 : day - 2))) & _days) == 0 && !isStoped) {
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

            if (isStoped){
                notifyBuilder = getNotification(false);
                startForeground(_notifyId, notifyBuilder.build());
                updateProvider(false);
            }else{
                updateProvider(true);
            }

            if (!(_isNotificationEnabled || isStoped)) {
                if (_interval != 0 || _period == 0) {
                    notifyBuilder = getNotification(false);
                } else {
                    notifyBuilder = getNotification(true);
                }
                startForeground(ServiceGpsTracking.this._notifyId, notifyBuilder.build());
                updateProvider(true);
            }

            if (_isNotificationEnabled && isStoped) {
                stopForeground(false);
                _notificationManager.cancel(_notifyId);
                _isNotificationEnabled = false;
                updateProvider(false);
            }






            stopForeground(false);
            _notificationManager.cancel(_notifyId);
            _isNotificationEnabled = false;


            if (!((_gpsLatitude != 0.0d && _gpsLongitude != 0.0d) || isStoped)) {
                Log.i(TAGLOG_GPS, "The current position is not recorded, the coordinates received incorrectly");
                isStoped = true;
            }

            if (!(dataRepository != null || isStoped)) {
                Log.e(TAGLOG_GPS, "An error occurred while data repository in ServiceGpsTracking method");
                isStoped = true;
            }

            if (!isStoped) {
                latitude = Gps.CorrectGPSDegree(_gpsLatitude);
                longitude = Gps.CorrectGPSDegree(_gpsLongitude);

                if (!(((LocationManager) getSystemService(LOCATION_SERVICE))
                        .isProviderEnabled(Provider.PROVIDER_GPS))) {
                    latitude = -100.0d;
                    longitude = -100.0d;
                }
                saveLastLocation(timeStamp, latitude, longitude, _gpsLocationSource);

                dataRepository.insertTrackPoint(
                        new LocationPoint(timeStamp.getTime(), latitude, longitude, true));

            }
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
        _context = this;
        tickHandler = new Handler();
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

    private void saveLastLocation(Calendar dt, double latitude, double longitude, int locationSource) {
        Intent intent = new Intent(GpsTracking.INITIALIZER_ACTION);
        ArrayList<String> stringParams = new ArrayList();
        stringParams.add(serviceStringPrefs.LATITUDE.getID(), Double.toString(latitude));
        stringParams.add(serviceStringPrefs.LONGTITUDE.getID(), Double.toString(longitude));
        stringParams.add(serviceStringPrefs.LOCATION_SOURCE.getID(), Integer.toString(locationSource));
        intent.putExtra(GpsTracking.STRING_PARAMS_FROM_SERVICE, stringParams);
        intent.putExtra(GpsTracking.LONG_PARAMS, dt.getTimeInMillis());
        sendBroadcast(intent);
    }

    private Builder getNotification(boolean isOk) {
        int i = R.string.service_tracking_message;
        _isNotificationEnabled = true;
        PendingIntent pendingIntent = PendingIntent.
                getActivity(this, 0, new Intent(this, GpsTracking.class), 0);
        PendingIntent errorPendingIntent = PendingIntent.
                getActivity(this, 0, new Intent(this, GpsTrackingNotification.class)
                        .putExtra("fromServiceGpsTrackingNotify", true), 0);

        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Builder builder = new Builder(this);
        if (!isOk) {
            pendingIntent = errorPendingIntent;
        }

        builder = builder.setContentIntent(pendingIntent).setContentTitle(getString(R.string.app_name) + " |" +
                        getString(R.string.gps_tracer_name))
                .setContentText(getText(isOk ? R.string.service_tracking_message : R.string.service_tracking_error_message));
        if (!isOk) {
            i = R.string.service_tracking_error_message;
        }
        return builder.setTicker(getString(i)).setSmallIcon(isOk ?
                R.drawable.ic_gps_track_connect :
                R.drawable.ic_gps_track_not_connect, 1)
                .setLargeIcon(notificationLargeIconBitmap)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setOngoing(true).setAutoCancel(true);
    }


    private void renewNotification(int isInitialize) {

        Calendar timeStamp = Calendar.getInstance();
        timeStamp.setTimeInMillis(getCurrentTime());
        int time = (timeStamp.get(11) * 60) + timeStamp.get(12);
        int day = timeStamp.get(7);
        if (_interval != 0 && _period != 0 &&
                ((LocationManager)getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS)) {
            Label_0270: {
                if ((isInitialize == 0 || isInitialize == 1) && (_startTime == _endTime ||
                        (time >= _startTime && time < _endTime))) {
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
            if ((isInitialize == 0 || isInitialize == 1) && (_startTime == _endTime ||
                    (time >= _startTime && time < _endTime))) {
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

    private void setNewPreference(int isInitialize, int[] integerParams,
                                  boolean[] booleanParams, ArrayList<String> stringParams) {
        if (isInitialize == 0 || isInitialize == 2) {
            _startTime = integerParams[integerPrefs.TIMESTART.getID()]  & SupportMenu.USER_MASK;
            _endTime = integerParams[integerPrefs.TIMEEND.getID()] & SupportMenu.USER_MASK;
            _interval = integerParams[integerPrefs.INTERVAL.getID()] * 1000;
            _days = integerParams[integerPrefs.DAYS.getID()];
            _period = integerParams[integerPrefs.PERIOD.getID()] * 1000;
            if (isInitialize == 2) {
                startForeground(_notifyId, getNotification(true).build());
                if (VERSION.SDK_INT > 22) {
                    stopForeground(false);
                }
                startTickTimer(true);
            }
        }
        renewNotification(isInitialize);
    }

    private void setReceiver() {

        serviceReceiver = new BroadcastReceiver(){

            public void onReceive(Context context, Intent intent) {
                Log.i(TAGLOG_GPS, "On receive serviceBroadcaster");

                String Action = intent.getStringExtra(GpsTracking.RECEIVER_ACTION);
                int[] IntegerParams = intent.getIntArrayExtra(GpsTracking.NUMERIC_PARAMS);
                boolean[] BooleanParams = intent.getBooleanArrayExtra(GpsTracking.BOOLEAN_PARAMS);
                ArrayList<String> StringParams = intent.getStringArrayListExtra(GpsTracking.STRING_PARAMS);
                if (Action.equals(GpsTracking.RENEW_PREFERENCES)) {
                    boolean isTTStartedBefore = _isTickTimerStarted;
                    startTickTimer(false);
                    setNewPreference(0, IntegerParams, BooleanParams, StringParams);
                    startTickTimer(isTTStartedBefore);
                }
                if (Action.equals(GpsTracking.CHECK_PREFERENCES)) {
                    setNewPreference(1, IntegerParams, BooleanParams, StringParams);
                }
                if (Action.equals(GpsTracking.SET_PREFERENCES)) {
                    setNewPreference(2, IntegerParams, BooleanParams, StringParams);
                }
            }
        };

        this.registerReceiver(serviceReceiver, new IntentFilter(GpsTracking.SERVICE_ACTION));
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
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!_isTickTimerStarted) {
            Log.i(TAGLOG_GPS, "Start service ServiceGpsTracking ");
            _notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            _notifyId = startId;
            setReceiver();
            updateProvider(true);
        }
        return Service.START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAGLOG_GPS, "Destroy service ServiceGpsTracking ");
        unregisterReceiver(serviceReceiver);
        updateProvider(false);
        startTickTimer(false);
    }

    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}