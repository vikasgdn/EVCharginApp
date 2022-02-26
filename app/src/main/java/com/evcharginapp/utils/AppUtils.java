package com.evcharginapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.evcharginapp.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AppUtils {

    /* renamed from: DF */
    public static DecimalFormat f153DF = new DecimalFormat("0.00");
    private static final String SCHEME = "package";
    public static boolean startSettingActivity;


    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService("input_method");
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(activity);
        }
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }



    public static boolean isValidEmail(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(charSequence).matches();
    }

    public static String getFormatedDateWithTime(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return "N/A";
            }
            return new SimpleDateFormat("EEE, d MMM yyyy, hh:mm aa").format(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(str));
        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    public static int convertDpToPixel(float f, Context context) {
        return (int) (f * (((float) context.getResources().getDisplayMetrics().densityDpi) / 160.0f));
    }

    public static float convertPixelsToDp(float f, Context context) {
        return f / (((float) context.getResources().getDisplayMetrics().densityDpi) / 160.0f);
    }

    public static String distanceBetweenLocationsInMiles(double d, double d2, double d3, double d4) {
        float[] fArr = new float[1];
        Location.distanceBetween(d, d2, d3, d4, fArr);
        return String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(fArr[0] * 6.213712E-4f)});
    }

    public static String formatDateForTrackOrder(String str) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parse = simpleDateFormat.parse(str);
            System.out.println(str);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM/dd/yyyy_hh:mm a");
            if (parse != null) {
                return simpleDateFormat2.format(parse);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentDateWithTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(date);
    }

    public static String formatDateInUSLocale(String str) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parse = simpleDateFormat.parse(str);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            if (parse != null) {
                return simpleDateFormat2.format(parse);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDateInUSLocaleOnlyDate(String str) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parse = simpleDateFormat.parse(str);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
            if (parse != null) {
                return simpleDateFormat2.format(parse);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDateInDatePickerOnlyDate(String str) {
        try {
            Date parse = new SimpleDateFormat("dd/MM/yyyy").parse(str);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            if (parse != null) {
                return simpleDateFormat.format(parse);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDateLocaleOnlyTime(String str) {
        try {
            Date parse = new SimpleDateFormat("HH:mm").parse(str);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
            if (parse != null) {
                return simpleDateFormat.format(parse);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void toastLongDisplay(Activity activity, String message) {
        if (message != null && !message.equals("") && activity != null) {
            Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
            snack.setDuration(4000);
            snack.show();
        }
    }
    public static void toast(Activity activity, String message) {
        if (message != null && !message.equals("") && activity != null) {
            Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snack.getView();
            group.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary));
            snack.setDuration(3000);
            snack.show();
        }
    }

    public String simpleTimeFormator(String str) {
        Date date;
        try {
            date = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss ZZZZ").parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        return new SimpleDateFormat("EEE yyyy-MM-dd hh:mm a").format(date);
    }

    public static Bitmap resizeImage(Bitmap bitmap, int i, int i2) {
        if (i2 <= 0 || i <= 0) {
            return bitmap;
        }
        float width = ((float) bitmap.getWidth()) / ((float) bitmap.getHeight());
        float f = (float) i;
        float f2 = (float) i2;
        if (f / f2 > width) {
            i = (int) (f2 * width);
        } else {
            i2 = (int) (f / width);
        }
        return Bitmap.createScaledBitmap(bitmap, i, i2, true);
    }


    public static long calculateIntervalTime()
    {
        long startTimeInMilli=0,T=System.currentTimeMillis(), W=300000;
        long offSet=getCurrentTimezoneOffset();
        startTimeInMilli=(T-((T-offSet) % W));
        Log.e("START TIME IN ","MILLI ==> "+startTimeInMilli);
        return startTimeInMilli;
    }

    public static long getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
         return offsetInMillis;
    }

    public static JSONArray getStaticJSON()
    {
        JSONArray jsonArray=new JSONArray();
        try {
            List<String> stringList=new ArrayList<>();
            stringList.add("awi_critical");
            stringList.add("awi_high");
            stringList.add("awi_medium");
            stringList.add("awi_low");

            for (int i=0;i<stringList.size();i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("awi_severity", stringList.get(i));
                jsonArray.put(jsonObject);
            }
            return jsonArray;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return jsonArray;
        }
    }

    public static JSONArray getStaticJSONForPreview(String severity)
    {
        JSONArray jsonArray=new JSONArray();
        try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("awi_severity", severity);
                jsonArray.put(jsonObject);
            }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return jsonArray;
    }


}
