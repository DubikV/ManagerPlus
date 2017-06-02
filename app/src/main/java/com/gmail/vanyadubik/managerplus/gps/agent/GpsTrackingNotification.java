//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//
//import android.app.Activity;
//import android.app.AlertDialog.Builder;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
//import android.content.Intent;
//import android.location.LocationManager;
//import android.os.Bundle;
//import java.util.Locale;
//import ru.agentplus.agentp2.C0285R;
//import com.gmail.vanyadubik.managerplus.gps.agent.SharedStorage;
//import ru.agentplus.ftpclient.BuildConfig;
//import ru.agentplus.location.Provider;
//
//public class GpsTrackingNotification extends Activity {
//
//    /* renamed from: ru.agentplus.tracking.GpsTrackingNotification.1 */
//    class C04721 implements OnClickListener {
//        C04721() {
//        }
//
//        public void onClick(DialogInterface dialog, int id) {
//            dialog.cancel();
//            GpsTrackingNotification.this.finish();
//        }
//    }
//
//    /* renamed from: ru.agentplus.tracking.GpsTrackingNotification.2 */
//    class C04732 implements OnClickListener {
//        C04732() {
//        }
//
//        public void onClick(DialogInterface dialog, int id) {
//            Intent intent = new Intent("android.intent.action.VIEW");
//            intent.setAction("android.settings.LOCATION_SOURCE_SETTINGS");
//            intent.addCategory("android.intent.category.DEFAULT");
//            GpsTrackingNotification.this.startActivity(intent);
//            dialog.cancel();
//            GpsTrackingNotification.this.finish();
//        }
//    }
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(C0285R.layout.gps_tracking_notification);
//    }
//
//    public void onResume() {
//        super.onResume();
//        if (getIntent().getBooleanExtra("fromServiceGpsTrackingNotify", false)) {
//            getIntent().removeExtra("fromServiceGpsTrackingNotify");
//            boolean isGPSEnabled = ((LocationManager) getSystemService("location")).isProviderEnabled(Provider.PROVIDER_GPS);
//            Locale locale = Locale.US;
//            String str = "%s%s%s%s";
//            Object[] objArr = new Object[4];
//            objArr[0] = SharedStorage.getInteger(this, "gpsTrackingInterval", 0) == 0 ? getResources().getString(C0285R.string.service_tracking_null_interval) : BuildConfig.VERSION_NAME;
//            objArr[1] = SharedStorage.getInteger(this, "gpsTrackingPeriod", 0) == 0 ? getResources().getString(C0285R.string.service_tracking_null_period) : BuildConfig.VERSION_NAME;
//            objArr[2] = !isGPSEnabled ? getResources().getString(C0285R.string.service_tracking_gps_disabled) : BuildConfig.VERSION_NAME;
//            objArr[3] = SharedStorage.getString(this, "gpsTrackingServerAddress", BuildConfig.VERSION_NAME).equals(BuildConfig.VERSION_NAME) ? getResources().getString(C0285R.string.service_tracking_null_address) : BuildConfig.VERSION_NAME;
//            String notifyText = String.format(locale, str, objArr);
//            Builder builder = new Builder(this);
//            builder.setTitle(C0285R.string.service_tracking_error_message).setMessage(notifyText).setCancelable(false).setNegativeButton(C0285R.string.device_info_dialog_ok, new C04721());
//            if (!isGPSEnabled) {
//                builder.setNeutralButton(C0285R.string.button_goto_gps_settings, new C04732());
//            }
//            builder.create().show();
//        }
//    }
//}