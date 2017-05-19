//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//import android.app.ActivityManager;
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Paint.Align;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.os.Build.VERSION;
//import android.os.Environment;
//import android.os.StatFs;
//import android.text.Layout.Alignment;
//import android.text.SpannableString;
//import android.text.StaticLayout;
//import android.text.TextPaint;
//import android.util.DisplayMetrics;
//import android.util.TypedValue;
//import android.view.View;
//import android.view.Window;
//import java.io.File;
//import ru.agentplus.apwnd.controls.graphics.Font;
//
//public class Utils {
//    private static final int ALIGNMENT_CENTER = 2;
//    private static final int ALIGNMENT_LEFT = 1;
//    private static final int ALIGNMENT_RIGHT = 0;
//    public static final int IO_BUFFER_SIZE = 8192;
//
//    private Utils() {
//    }
//
//    public static int getBitmapSize(Bitmap bitmap) {
//        return bitmap.getRowBytes() * bitmap.getHeight();
//    }
//
//    public static boolean isExternalStorageRemovable() {
//        return true;
//    }
//
//    public static File getExternalCacheDir(Context context) {
//        if (hasExternalCacheDir()) {
//            return context.getExternalCacheDir();
//        }
//        return new File(Environment.getExternalStorageDirectory().getPath() + ("/Android/data/" + context.getPackageName() + "/cache/"));
//    }
//
//    public static long getUsableSpace(File path) {
//        StatFs stats = new StatFs(path.getPath());
//        return ((long) stats.getBlockSize()) * ((long) stats.getAvailableBlocks());
//    }
//
//    public static int getMemoryClass(Context context) {
//        return ((ActivityManager) context.getSystemService("activity")).getMemoryClass();
//    }
//
//    public static boolean hasExternalCacheDir() {
//        return VERSION.SDK_INT >= 8;
//    }
//
//    public static boolean hasActionBar() {
//        return false;
//    }
//
//    public static boolean isActiveDialog(Dialog dialog) {
//        if (!dialog.isShowing()) {
//            return false;
//        }
//        Window dialogWindow = dialog.getWindow();
//        if (dialogWindow == null) {
//            return false;
//        }
//        View decorView = dialogWindow.getDecorView();
//        if (decorView != null) {
//            return decorView.hasWindowFocus();
//        }
//        return false;
//    }
//
//    public static float getTextWidth(Context context, String text, Font font) {
//        TextPaint textPaint = new TextPaint(ALIGNMENT_LEFT);
//        textPaint.setAntiAlias(true);
//        SpannableString s = new SpannableString(text);
//        DisplayMetrics displayMetrics = SystemInfo.getDisplayMetrics(context);
//        textPaint.setTypeface(font.getTypeface());
//        textPaint.setTextSize(TypedValue.applyDimension(font.getFontSizeUnit(), font.getFontSizeBase(), displayMetrics));
//        font.fillSpannable(s);
//        return StaticLayout.getDesiredWidth(s, textPaint);
//    }
//
//    public static Point getTextSize(Context context, String text, Font font, int maxWidth, int align) {
//        TextPaint textPaint = new TextPaint(ALIGNMENT_LEFT);
//        textPaint.setTextAlign(Align.RIGHT);
//        Alignment alignment = Alignment.ALIGN_NORMAL;
//        if (align == ALIGNMENT_LEFT) {
//            textPaint.setTextAlign(Align.LEFT);
//            alignment = Alignment.ALIGN_OPPOSITE;
//        } else if (align == ALIGNMENT_CENTER) {
//            alignment = Alignment.ALIGN_CENTER;
//            textPaint.setTextAlign(Align.CENTER);
//        }
//        SpannableString spannableString = new SpannableString(text);
//        if (font != null) {
//            DisplayMetrics displayMetrics = SystemInfo.getDisplayMetrics(context);
//            textPaint.setTypeface(font.getTypeface());
//            textPaint.setTextSize(TypedValue.applyDimension(font.getFontSizeUnit(), font.getFontSizeBase(), displayMetrics));
//            font.fillSpannable(spannableString);
//        }
//        if (maxWidth < 0 || maxWidth == Integer.MAX_VALUE) {
//            Rect bounds = new Rect();
//            textPaint.getTextBounds(text, ALIGNMENT_RIGHT, text.length(), bounds);
//            return new Point(bounds.right - bounds.left, bounds.bottom - bounds.top);
//        }
//        StaticLayout staticLayout = new StaticLayout(spannableString, textPaint, maxWidth, alignment, 1.0f, 0.0f, false);
//        float width = 0.0f;
//        for (int i = ALIGNMENT_RIGHT; i < staticLayout.getLineCount(); i += ALIGNMENT_LEFT) {
//            float subTextWidth = textPaint.measureText(spannableString, staticLayout.getLineStart(i), staticLayout.getLineEnd(i));
//            if (subTextWidth > width) {
//                width = subTextWidth;
//            }
//        }
//        if (width == 0.0f) {
//            width = (float) staticLayout.getWidth();
//        }
//        return new Point((int) Math.ceil((double) width), staticLayout.getHeight());
//    }
//}