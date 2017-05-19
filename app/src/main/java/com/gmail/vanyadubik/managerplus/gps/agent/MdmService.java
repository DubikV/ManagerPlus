//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//
//import android.content.ComponentName;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Build.VERSION;
//import android.os.Bundle;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import org.apache.commons.net.io.Util;
//import com.gmail.vanyadubik.managerplus.gps.agent.BuildConfig;
//
//public class MdmService {
//    private static final String MDM_PROVIDER = "ru.agentplus.mdm.MdmProvider";
//    private static final String URL_ENROLLPLATFORMINFO = "content://ru.agentplus.mdm.MdmProvider/EnrollPlatformInfo";
//    private static final String URL_ERROR = "content://ru.agentplus.mdm.MdmProvider/Error";
//    private static final String URL_GETDEVICE = "content://ru.agentplus.mdm.MdmProvider/GetDevice";
//    private static final String URL_GETDEVICEID = "content://ru.agentplus.mdm.MdmProvider/GetDeviceId";
//    private static final String URL_MDM_PROVIDER = "content://ru.agentplus.mdm.MdmProvider";
//    private static final String URL_SETDEVICEGUID = "content://ru.agentplus.mdm.MdmProvider/SetDeviceGUID";
//    private static final String URL_SIGNDATA = "content://ru.agentplus.mdm.MdmProvider/SignData";
//    private static final String URL_UPDATEPLATFORMINFO = "content://ru.agentplus.mdm.MdmProvider/UpdatePlatformInfo";
//
//    public static void updatePlatformInfo(Context context, String platformVersion, long databaseCapacity) {
//        Uri mdmProvider = Uri.parse(URL_UPDATEPLATFORMINFO);
//        ContentValues values = new ContentValues();
//        values.put("PlatformVersion", platformVersion);
//        values.put("DatabaseCapacity", Long.valueOf(databaseCapacity));
//        context.getContentResolver().update(mdmProvider, values, null, null);
//    }
//
//    public static boolean makeEnroll(Context context, String activationCode, String worker) {
//        Uri mdmProvider = Uri.parse(URL_ENROLLPLATFORMINFO);
//        ContentValues values = new ContentValues();
//        values.put("ActivationCode", activationCode);
//        values.put("Worker", worker);
//        if (context.getContentResolver().update(mdmProvider, values, null, null) == 1) {
//            return true;
//        }
//        return false;
//    }
//
//    public static String[] getDevice(Context context) {
//        String[] result = null;
//        Cursor c = context.getContentResolver().query(Uri.parse(URL_GETDEVICE), null, null, null, BuildConfig.VERSION_NAME);
//        if (c != null && c.moveToFirst()) {
//            int columnCount = c.getColumnCount();
//            result = new String[columnCount];
//            for (int i = 0; i < columnCount; i++) {
//                result[i] = c.getString(i);
//            }
//        }
//        return result;
//    }
//
//    public static String getDeviceId(Context context) {
//        Cursor c = context.getContentResolver().query(Uri.parse(URL_GETDEVICEID), null, null, null, BuildConfig.VERSION_NAME);
//        if (c == null || !c.moveToFirst()) {
//            return BuildConfig.VERSION_NAME;
//        }
//        return c.getString(0);
//    }
//
//    public static String getError(Context context) {
//        Cursor c = context.getContentResolver().query(Uri.parse(URL_ERROR), null, null, null, BuildConfig.VERSION_NAME);
//        if (c == null || !c.moveToFirst()) {
//            return BuildConfig.VERSION_NAME;
//        }
//        return c.getString(0);
//    }
//
//    public static String signData(Context context, String data) {
//        Uri mdmProvider = Uri.parse(URL_SIGNDATA);
//        if (VERSION.SDK_INT >= 11) {
//            Bundle bundleSignData = new Bundle();
//            bundleSignData.putString("Data", data);
//            Bundle bundleRes = context.getContentResolver().call(mdmProvider, "SignData", null, bundleSignData);
//            if (bundleRes.containsKey("Sign")) {
//                return bundleRes.getString("Sign");
//            }
//            return null;
//        }
//        ContentValues cv = new ContentValues();
//        cv.put("Data", data);
//        context.getContentResolver().update(mdmProvider, cv, null, null);
//        Cursor c = context.getContentResolver().query(mdmProvider, null, null, null, BuildConfig.VERSION_NAME);
//        if (c == null || !c.moveToFirst()) {
//            return null;
//        }
//        return c.getString(0);
//    }
//
//    public static String createAuthorizationToken(Context context, String data, String deviceId) {
//        String signedData = signData(context, data);
//        return String.format("token %s:%s", new Object[]{deviceId, signedData});
//    }
//
//    public static String readFully(InputStream entityResponse) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try {
//            byte[] buffer = new byte[Util.DEFAULT_COPY_BUFFER_SIZE];
//            while (true) {
//                int length = entityResponse.read(buffer);
//                if (length == -1) {
//                    break;
//                }
//                baos.write(buffer, 0, length);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return baos.toString();
//    }
//
//    private static Intent getMdmServiceIntent() {
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName(AgentP2.licensePackageName, MDM_PROVIDER));
//        return intent;
//    }
//
//    public static void restartMdmService(Context context) {
//        context.stopService(getMdmServiceIntent());
//        context.startService(getMdmServiceIntent());
//    }
//
//    public static void setDeviceGUID(Context context, String GUID) {
//        Uri mdmProvider = Uri.parse(URL_SETDEVICEGUID);
//        ContentValues values = new ContentValues();
//        values.put("GUID", GUID);
//        context.getContentResolver().update(mdmProvider, values, null, null);
//    }
//}
