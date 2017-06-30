package com.gmail.vanyadubik.managerplus.gps.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.internal.view.SupportMenu;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.StartActivity;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.location.AndroidLocationService;
import com.gmail.vanyadubik.managerplus.gps.location.AndroidLocationUpdateListener;
import com.gmail.vanyadubik.managerplus.gps.location.AndroidPlayLocationService;
import com.gmail.vanyadubik.managerplus.gps.location.AndroidPlayLocationUpdateListener;
import com.gmail.vanyadubik.managerplus.gps.location.GooglePlayLocationService;
import com.gmail.vanyadubik.managerplus.gps.location.GooglePlayLocationUpdateListener;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.booleanPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.doublePrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.integerPrefs;
import com.gmail.vanyadubik.managerplus.gps.service.GpsTracking.serviceStringPrefs;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_GPS_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.MIN_DISTANCE_WRITE_TRACK;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_GPS;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTrackingNotification.ERROR_LOCATION_GPS;
import static com.gmail.vanyadubik.managerplus.gps.service.GpsTrackingNotification.ERROR_SETTING_GPS;

public class ServiceGpsTracking extends Service {

    @Inject
    DataRepository dataRepository;


    private Context _context;
    private int _typeService;
    private int _interval;
    private int _startTime;
    private int _endTime;
    private int _days;
    private int _gpsLocationSource;
    private double _gpsAccury;
    private int _gpsStatus;
    private boolean _isNotificationEnabled;
    private boolean _isTickTimerStarted;
    private boolean _devMode;
    private GooglePlayLocationService _googlePlayLocationService;
    private AndroidPlayLocationService _androidPlayLocationService;
    private AndroidLocationService _androidLocationService;
    private NotificationManager _notificationManager;
    private int _notifyId;
    private BroadcastReceiver serviceReceiver;
    private Location _currentBestLocation, _location;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
    private Handler tickHandler;
    private Runnable tickTimer;

    class TickTimer implements Runnable {
        TickTimer() {
        }

        public void run() {
            int intervalRun;
            Builder notifyBuilder;
            boolean isStoped = false;
            Calendar timeStamp = Calendar.getInstance();
            timeStamp.setTimeInMillis(getCurrentTime());
            int time = (timeStamp.get(Calendar.HOUR_OF_DAY) * 60) + timeStamp.get(Calendar.MINUTE);
            int day = timeStamp.get(Calendar.DAY_OF_WEEK);

            intervalRun = Math.abs(_interval);

            if (_startTime != _endTime && (time < _startTime || time >= _endTime)) {
                Log.i(TAGLOG_GPS, "The current position has not been recorded. The current time is outside the working time limits");
                isStoped = true;
                intervalRun = (((time <= _endTime ?
                        ((1440 - _endTime) + _startTime) -
                                (time - _endTime) : _startTime - time) * 60) * 1000) -
                        (timeStamp.get(Calendar.SECOND) * 1000);

            }

            if (((day - 1) >_days) && !isStoped) {
                Log.i(TAGLOG_GPS, "The current position has not been recorded. The current day is outside the working days limits");
                isStoped = true;
                intervalRun = ((1440 - time) * 60) * 1000;
                while (intervalRun < 604800000) {
                    if (((day - 1) > 7)) {
                        break;
                    }
                    intervalRun += 86400000;
                    day++;
                }
                intervalRun = (intervalRun + ((_startTime * 60) * 1000));
            }

            if (isStoped){
                updateProvider(false);
                if (_isNotificationEnabled) {
                    _notificationManager.cancel(_notifyId);
                    _isNotificationEnabled = false;
                }
                stopForeground(true);
            }else{
                updateProvider(true);
            }

            if (_location == null || isStoped ||
                    !(((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS))) {
                Log.i(TAGLOG_GPS, "The current position is not recorded, the coordinates received incorrectly");
                notifyBuilder = getNotification(false, getString(R.string.service_tracking_error_location));
                startForeground(_notifyId, notifyBuilder.build());
                isStoped = true;
            }

            if (!(dataRepository != null || isStoped)) {
                Log.e(TAGLOG_GPS, "An error occurred while data repository in ServiceGpsTracking method");
                notifyBuilder = getNotification(false, getString(R.string.service_tracking_error_database));
                startForeground(_notifyId, notifyBuilder.build());
                isStoped = true;
            }

            if (_isNotificationEnabled && !isStoped) {
                if (_interval == 0) {
                    notifyBuilder = getNotification(false, getString(R.string.service_tracking_error_message));
                    startForeground(_notifyId, notifyBuilder.build());
                    isStoped = true;
                }
            }

            if (!isStoped) {

                if (_devMode) {

                    notifyBuilder = getNotification(true, getMessageNotify(_location, timeStamp.getTime().getTime(), true));
                    startForeground(_notifyId, notifyBuilder.build());
                }

                if (isBettherLocation(_location)) {

                    notifyBuilder = getNotification(true, getMessageNotify(_location, timeStamp.getTime().getTime(), false));
                    startForeground(_notifyId, notifyBuilder.build());

                    saveLastLocation(timeStamp, _location.getLatitude(), _location.getLongitude(), _gpsLocationSource);

                    dataRepository.insertTrackPoint(
                            new LocationPoint(timeStamp.getTime(),
                                    _location.getLatitude(),
                                    _location.getLongitude(), true));
                }

            }
            if (intervalRun < _interval) {
                intervalRun = _interval;
                if (intervalRun < 1000) {
                    intervalRun = 1000;
                }
            }
            tickHandler.postDelayed(tickTimer, (long) intervalRun);
        }

    }

    private String getMessageNotify(Location location, long time, boolean devMode){
        String nameService;
        if (devMode) {
            if (_typeService == TypeServiceGPS.SERVICE_GPS_ANDROID.getIndex()) {
                nameService = " |F |A ";
            } else if (_typeService == TypeServiceGPS.SERVICE_GPS_ANDROID_PLAY.getIndex()) {
                nameService = " |F |AP ";
            } else if (_typeService == TypeServiceGPS.SERVICE_GPS_GOOGLE_PLAY.getIndex()) {
                nameService = " |F |GP ";
            } else {
                nameService = " |F |E ";
            }
        }else {
            nameService = " ";
        }
        return dateFormat.format(time) + nameService
                + new DecimalFormat("#.#").format(location.getAccuracy()) + " |"
                + " " + new DecimalFormat("#.####").format(location.getLatitude())
                + " : " + new DecimalFormat("#.####").format(location.getLongitude());
    }

    public ServiceGpsTracking() {
        _interval = 0;
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

    private Builder getNotification(boolean isOk, String message) {

        Intent notificationIntent;
        PendingIntent pendingIntent;

        _isNotificationEnabled = true;

        if(isOk) {
            notificationIntent = new Intent(this, StartActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            notificationIntent = new Intent(this, GpsTrackingNotification.class);
            notificationIntent.putExtra(GpsTracking.SERVICE_GPS_NOTIFY, _location == null ? ERROR_LOCATION_GPS : ERROR_SETTING_GPS);

            pendingIntent = PendingIntent.
                    getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Builder builder = new Builder(this);
        builder = builder.setContentIntent(pendingIntent).setContentTitle(getString(R.string.app_name) + " |" +
                getString(R.string.gps_tracer_name))
                .setContentText(message);

        return builder.setTicker(message).setSmallIcon(isOk ?
                R.drawable.ic_gps_track_connect :
                R.drawable.ic_gps_track_not_connect, 1)
                .setLargeIcon(notificationLargeIconBitmap)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setOngoing(true).setAutoCancel(true);
    }


    private void renewNotification(int isInitialize) {

        Calendar timeStamp = Calendar.getInstance();
        timeStamp.setTimeInMillis(getCurrentTime());
        int time = (timeStamp.get(Calendar.HOUR_OF_DAY) * 60) + timeStamp.get(Calendar.MINUTE);
        if (_interval != 0 && ((LocationManager)getSystemService(LOCATION_SERVICE)).isProviderEnabled(Provider.PROVIDER_GPS)) {
            if ((isInitialize == 0 || isInitialize == 1) && (_startTime == _endTime ||
                    (time >= _startTime && time < _endTime)) && _typeService < 3) {
                return;
            }
        }
        _notificationManager.cancel(_notifyId);
        _isNotificationEnabled = false;
        _notificationManager.notify(_notifyId, getNotification(false, getString(R.string.service_tracking_error_message)).build());
    }

    private void setNewPreference(int isInitialize, int[] integerParams, double[] doubleParams,
                                  boolean[] booleanParams, ArrayList<String> stringParams) {
        if (isInitialize == 0 || isInitialize == 2) {
            _startTime = integerParams[integerPrefs.TIMESTART.getID()]  & SupportMenu.USER_MASK;
            _endTime = integerParams[integerPrefs.TIMEEND.getID()] & SupportMenu.USER_MASK;
            _interval = integerParams[integerPrefs.INTERVAL.getID()] * 1000;
            _days = integerParams[integerPrefs.DAYS.getID()];
            _typeService = integerParams[integerPrefs.TYPE_SERVICE.getID()];
            _gpsAccury = doubleParams[doublePrefs.ACCURY.getID()];
            _devMode = booleanParams[booleanPrefs.DEVELOP_MODE.getID()];
            if (isInitialize == 2) {
                startForeground(_notifyId, getNotification(true, getString(R.string.service_tracking_message)).build());
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
                double[] DoubleParams = intent.getDoubleArrayExtra(GpsTracking.DOUBLE_PARAMS);
                boolean[] BooleanParams = intent.getBooleanArrayExtra(GpsTracking.BOOLEAN_PARAMS);
                ArrayList<String> StringParams = intent.getStringArrayListExtra(GpsTracking.STRING_PARAMS);
                if (Action.equals(GpsTracking.RENEW_PREFERENCES)) {
                    boolean isTTStartedBefore = _isTickTimerStarted;
                    startTickTimer(false);
                    setNewPreference(0, IntegerParams, DoubleParams, BooleanParams, StringParams);
                    startTickTimer(isTTStartedBefore);
                }
                if (Action.equals(GpsTracking.CHECK_PREFERENCES)) {
                    setNewPreference(1, IntegerParams, DoubleParams, BooleanParams, StringParams);
                }
                if (Action.equals(GpsTracking.SET_PREFERENCES)) {
                    setNewPreference(2, IntegerParams, DoubleParams, BooleanParams, StringParams);
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

            if(_typeService == TypeServiceGPS.SERVICE_GPS_ANDROID.getIndex()) {

                if(_androidPlayLocationService != null){
                    if (_androidPlayLocationService.isStarted()) {
                        _androidPlayLocationService.stopLocationUpdates();
                    }
                    _androidPlayLocationService = null;
                }

                if (_googlePlayLocationService != null) {
                    if (_googlePlayLocationService.isStarted()) {
                        _googlePlayLocationService.stopLocationUpdates();
                        _googlePlayLocationService.closeGoogleApi();
                    }
                    _googlePlayLocationService = null;
                }

                if (_androidLocationService == null) {
                    _androidLocationService = new AndroidLocationService(this, new AndroidLocationUpdateListener() {

                        private void onGpsStatusChanged(int status) {
                            if (status != 2) {
                                _location = null;
                            }
                        }

                        @Override
                        public void cannotReceiveLocationUpdates(String exception) {
                            Log.e(TAGLOG_GPS, exception);
                        }

                        @Override
                        public void updateLocation(Location location) {
                            if (location != null) {
                                _location = location;
                                _gpsLocationSource = Provider.FromName(location.getProvider()).getIndex();
                                if (_gpsStatus != 2) {
                                    _gpsStatus = 2;
                                    onGpsStatusChanged(_gpsStatus);
                                }
                            }

                        }

                        @Override
                        public void startLocation(Location location) {
                        }

                        @Override
                        public void onProviderDisabledEnabled(Boolean using, String provider) {
                            if(using){
                                _gpsStatus = 2;
                                onGpsStatusChanged(_gpsStatus);
                                renewNotification(1);
                            }else{
                                _gpsStatus = 0;
                                onGpsStatusChanged(_gpsStatus);
                                renewNotification(1);
                            }

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                    });
                    _androidLocationService.setTimeInterval(0);
                    _androidLocationService.setDistance(0);

                }

                if (!_androidLocationService.isStarted()) {
                    _androidLocationService.startLocationUpdates();
                }
                return;
            }

            if(_typeService == TypeServiceGPS.SERVICE_GPS_ANDROID_PLAY.getIndex()) {

                if (_androidLocationService != null) {
                    if (_androidLocationService.isStarted()) {
                        _androidLocationService.stopLocationUpdates();
                    }
                    _androidLocationService = null;
                }

                if (_googlePlayLocationService != null) {
                    if (_googlePlayLocationService.isStarted()) {
                        _googlePlayLocationService.stopLocationUpdates();
                        _googlePlayLocationService.closeGoogleApi();
                    }
                    _googlePlayLocationService = null;
                }

                if (_androidPlayLocationService == null) {
                    _androidPlayLocationService = new AndroidPlayLocationService(this, new AndroidPlayLocationUpdateListener() {

                        @Override
                        public void canReceiveLocationUpdates() {
                        }

                        @Override
                        public void cannotReceiveLocationUpdates(String exception) {
                            Log.e(TAGLOG_GPS, exception);
                        }

                        @Override
                        public void updateLocation(LocationResult locationResult) {
                            if (locationResult != null) {
                                _location = locationResult.getLastLocation();
                                _gpsLocationSource = Provider.FromName(_location.getProvider()).getIndex();
                            }

                        }

                        @Override
                        public void startLocation(Location location) {
                        }

                    });
                    _androidPlayLocationService.setTypePriorityConnection(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    _androidPlayLocationService.setTimeInterval(0);
                    _androidPlayLocationService.setFastesInterval(0);
                    _androidPlayLocationService.setDistance(0);

                }

                if (!_androidPlayLocationService.isStarted()) {
                    _androidPlayLocationService.startLocationUpdates();
                }
                return;
            }

            if(_typeService == TypeServiceGPS.SERVICE_GPS_GOOGLE_PLAY.getIndex()) {

                if (_androidLocationService != null) {
                    if (_androidLocationService.isStarted()) {
                        _androidLocationService.stopLocationUpdates();
                    }
                    _androidLocationService = null;
                }

                if(_androidPlayLocationService != null){
                    if (_androidPlayLocationService.isStarted()) {
                        _androidPlayLocationService.stopLocationUpdates();
                    }
                    _androidPlayLocationService = null;
                }

                if (_googlePlayLocationService == null) {
                    _googlePlayLocationService = new GooglePlayLocationService(this, new GooglePlayLocationUpdateListener() {
                        @Override
                        public void canReceiveLocationUpdates() {
                        }

                        @Override
                        public void cannotReceiveLocationUpdates(String exception) {
                            Log.e(TAGLOG_GPS, exception);
                        }

                        @Override
                        public void updateLocation(Location location) {
                            if (location != null) {
                                _location = location;
                                _gpsLocationSource = Provider.FromName(location.getProvider()).getIndex();
                            }
                        }

                        @Override
                        public void startLocation(Location location) {
                        }

                    });
                    _googlePlayLocationService.setTypePriorityConnection(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    _googlePlayLocationService.setTimeInterval(0);
                    _googlePlayLocationService.setFastesInterval(0);
                    _googlePlayLocationService.setDistance(0);

                }

                if (!_googlePlayLocationService.isStarted()) {
                    _googlePlayLocationService.startLocationUpdates();
                }
                return;
            }

        } else {

            if (_androidLocationService != null) {
                if (_androidLocationService.isStarted()) {
                    _androidLocationService.stopLocationUpdates();
                }
                _androidLocationService = null;
            }

            if (_androidPlayLocationService != null) {
                if (_androidPlayLocationService.isStarted()) {
                    _androidPlayLocationService.stopLocationUpdates();
                }
                _androidPlayLocationService = null;
            }

            if (_googlePlayLocationService != null) {
                if (_googlePlayLocationService.isStarted()) {
                    _googlePlayLocationService.stopLocationUpdates();
                    _googlePlayLocationService.closeGoogleApi();
                }
                _googlePlayLocationService = null;
            }
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
            //_notifyId = startId;
            _notifyId = DEFAULT_NOTIFICATION_GPS_TRACER_ID;
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

    private Boolean isBettherLocation(Location location){

        if(!location.hasAccuracy() ||
                (location.getAccuracy() > _gpsAccury && _gpsAccury > 0.0)  ){
            return false;
        }
        if (_currentBestLocation == null) {
            _currentBestLocation = location;
            return true;
        }
        if(_currentBestLocation.distanceTo(location) > MIN_DISTANCE_WRITE_TRACK){

            if(((int)(_currentBestLocation.getLatitude()*10000)== (int)(location.getLatitude()*10000)) ||
                    ((int)(_currentBestLocation.getLongitude()*10000)== (int)(location.getLongitude()*10000))) {
                return false;
            }

            _currentBestLocation = location;

            return true;
        }
        return false;
    }
}