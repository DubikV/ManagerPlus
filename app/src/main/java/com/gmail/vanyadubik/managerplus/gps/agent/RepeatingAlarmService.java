//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences.Editor;
//import android.net.Uri;
//import android.os.SystemClock;
//import android.util.Base64;
//import com.google.common.net.HttpHeaders;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request.Builder;
//import com.squareup.okhttp.RequestBody;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.Locale;
//import com.gmail.vanyadubik.managerplus.gps.agent.Gps;
//import com.gmail.vanyadubik.managerplus.gps.agent.SharedStorage;
//import com.gmail.vanyadubik.managerplus.gps.agent.BuildConfig;
//import com.gmail.vanyadubik.managerplus.gps.agent.MessageDigestHelper;
//import com.gmail.vanyadubik.managerplus.gps.agent.OkHttpClientUtils;
//
//public class RepeatingAlarmService extends BroadcastReceiver {
//    public static final String ACTION_SEND_TRACK = "Send";
//    public static final String ACTION_WRITE_TRACK = "Write";
//    private static final String LacationLineArgs = "%04d-%02d-%02d %02d-%02d-%02d\t%.4f\t%.4f";
//    private static final String LacationLocationSourceArg = "\t%d";
//    private static final String LacationNewLine = "\r\n";
//    private static final String LacationSpeedArg = "\t%.2f";
//    public static String MY_TRACKING_ALARM;
//
//    class SendThread extends Thread {
//        private Context _context;
//
//        public SendThread(Context context) {
//            this._context = context;
//        }
//
//        public void run() {
//            File file = new File(ServiceGpsTracking.trackFile);
//            if (!file.exists()) {
//                return;
//            }
//            if (ServiceGpsTracking.serverType == 0) {
//                RepeatingAlarmService.this.Send(ServiceGpsTracking.serverAddress, ServiceGpsTracking.port, ServiceGpsTracking.trackFile, ServiceGpsTracking.ppcGuid);
//            } else if (ServiceGpsTracking.serverType == 1) {
//                String url = ServiceGpsTracking.serverAddress + ":" + ServiceGpsTracking.port + "/api/tracking/" + ServiceGpsTracking.erpId + "/" + ServiceGpsTracking.ppcGuid;
//                RequestBody body = RequestBody.create(MediaType.parse("text/plain"), file);
//                String sha256Base64 = Base64.encodeToString(MessageDigestHelper.getFileDigest("SHA-256", file.getPath()), 2);
//                try {
//                    if (new OkHttpClient().newCall(OkHttpClientUtils.getSignedRequest(this._context, new Builder().url(url).addHeader(HttpHeaders.DATE, new Date().toString()).addHeader("X-Content-SHA256", sha256Base64).post(body).build())).execute().isSuccessful()) {
//                        file.delete();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    native void Send(String str, int i, String str2, String str3);
//
//    static {
//        MY_TRACKING_ALARM = "ru.agentplus.agentp2.MY_TRACKING_ALARM";
//        System.loadLibrary("gnustl_shared");
//        System.loadLibrary("ApBackgroundExchange");
//    }
//
//    public void onReceive(Context context, Intent intentA) {
//        if (intentA.getAction().equals(MY_TRACKING_ALARM)) {
//            String action = intentA.getData().toString();
//            Intent intent;
//            if (action.equals(ACTION_WRITE_TRACK)) {
//                ServiceGpsTracking.lastAlarmTick = SystemClock.elapsedRealtime();
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                int time = (timestamp.getHours() * 60) + timestamp.getMinutes();
//                int day;
//                if (timestamp.getDay() == 0) {
//                    day = 6;
//                } else {
//                    day = timestamp.getDay() - 1;
//                }
//                if ((ServiceGpsTracking.startTime == ServiceGpsTracking.endTime || (time >= ServiceGpsTracking.startTime && time < ServiceGpsTracking.endTime)) && (ServiceGpsTracking.days & (1 << day)) != 0 && ServiceGpsTracking.gpsLatitude != 0.0d && ServiceGpsTracking.gpsLongitude != 0.0d) {
//                    File file = new File(ServiceGpsTracking.trackFile);
//                    try {
//                        if (!file.exists()) {
//                            file.createNewFile();
//                        }
//                        saveLastLocation(context, new Timestamp(ServiceGpsTracking.bGpsTime ? ServiceGpsTracking.gpsTime : System.currentTimeMillis()), Gps.CorrectGPSDegree(ServiceGpsTracking.gpsLatitude), Gps.CorrectGPSDegree(ServiceGpsTracking.gpsLongitude), ServiceGpsTracking.gpsSpeed, ServiceGpsTracking.gpsLocationSource);
//                        PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
//                        String locationLine = String.format(Locale.US, LacationLineArgs, new Object[]{Integer.valueOf(dt.getYear() + 1900), Integer.valueOf(dt.getMonth() + 1), Integer.valueOf(dt.getDate()), Integer.valueOf(dt.getHours()), Integer.valueOf(dt.getMinutes()), Integer.valueOf(dt.getSeconds()), Double.valueOf(latitude), Double.valueOf(longitude)});
//                        if (ServiceGpsTracking.bSpeed) {
//                            locationLine = locationLine + String.format(Locale.US, LacationSpeedArg, new Object[]{Double.valueOf(ServiceGpsTracking.gpsSpeed)});
//                        }
//                        if (ServiceGpsTracking.bLocationSource) {
//                            locationLine = locationLine + String.format(Locale.US, (ServiceGpsTracking.bSpeed ? BuildConfig.VERSION_NAME : "\t") + LacationLocationSourceArg, new Object[]{Integer.valueOf(ServiceGpsTracking.gpsLocationSource)});
//                        }
//                        printWriter.print(locationLine + LacationNewLine);
//                        printWriter.close();
//                        intent = new Intent(MY_TRACKING_ALARM, Uri.parse(ACTION_WRITE_TRACK), ServiceGpsTracking.getContext(), RepeatingAlarmService.class);
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ServiceGpsTracking.getContext(), ServiceGpsTracking.REQUEST_CODE, intent, 0);
//                        ServiceGpsTracking.alarmManager.cancel(PendingIntent.getBroadcast(ServiceGpsTracking.getContext(), ServiceGpsTracking.REQUEST_CODE, intent, 0));
//                        long currentTime = SystemClock.elapsedRealtime();
//                        long nextAlarmTick = currentTime + ((long) ServiceGpsTracking.getInterval());
//                        if (ServiceGpsTracking.lastAlarmTick == -1 || nextAlarmTick < currentTime) {
//                            ServiceGpsTracking.alarmManager.set(2, currentTime, pendingIntent);
//                        } else if (nextAlarmTick > currentTime) {
//                            ServiceGpsTracking.alarmManager.set(2, nextAlarmTick, pendingIntent);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else if (!action.equals(ACTION_SEND_TRACK)) {
//            } else {
//                if (ServiceGpsTracking.serverAddress == null || ServiceGpsTracking.trackFile == null || ServiceGpsTracking.ppcGuid == null) {
//                    ((AlarmManager) context.getSystemService(NotificationCompatApi21.CATEGORY_ALARM)).cancel(PendingIntent.getBroadcast(context, ServiceGpsTracking.REQUEST_CODE, new Intent(MY_TRACKING_ALARM, Uri.parse(ACTION_SEND_TRACK), context, RepeatingAlarmService.class), 0));
//                    return;
//                }
//                intent = new Intent(MY_TRACKING_ALARM, Uri.parse(ACTION_SEND_TRACK), ServiceGpsTracking.getContext(), RepeatingAlarmService.class);
//                PendingIntent pendingIntentSender = PendingIntent.getBroadcast(ServiceGpsTracking.getContext(), ServiceGpsTracking.REQUEST_CODE, intent, 0);
//                ServiceGpsTracking.alarmManager.cancel(PendingIntent.getBroadcast(ServiceGpsTracking.getContext(), ServiceGpsTracking.REQUEST_CODE, intent, 0));
//                ServiceGpsTracking.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) ServiceGpsTracking.getPeriod()), pendingIntentSender);
//                new SendThread(context).start();
//            }
//        }
//    }
//
//    public static boolean canWriteFile(String filePath) {
//        File file = new File(filePath);
//        return file.exists() && file.canWrite();
//    }
//
//    public static boolean canReadFile(String filePath) {
//        File file = new File(filePath);
//        return file.exists() && file.canRead();
//    }
//
//    public void saveLastLocation(Context context, Timestamp dt, double latitude, double longitude, double speed, int locationSource) {
//        Editor editor = context.getSharedPreferences(SharedStorage.APP_PREFS, 0).edit();
//        editor.putLong(GpsTracking.LAST_DATE_KEY, dt.getTime());
//        editor.putString(GpsTracking.LAST_LATITUDE_KEY, Double.toString(latitude));
//        editor.putString(GpsTracking.LAST_LONGITUDE_KEY, Double.toString(longitude));
//        editor.putString(GpsTracking.LAST_SPEED_KEY, Double.toString(speed));
//        editor.putString(GpsTracking.LAST_LOCATIONSOURCE_KEY, Integer.toString(locationSource));
//        editor.commit();
//    }
//}