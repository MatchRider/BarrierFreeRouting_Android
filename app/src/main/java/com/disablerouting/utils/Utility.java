package com.disablerouting.utils;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.overlay.OverlayItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utility {
    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(AppCompatActivity activity) {
        if (activity!=null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static TranslateAnimation translate(float fromX, float toX, float fromY,
                                        float toY, int ms) {
        TranslateAnimation transAnim = new TranslateAnimation(fromX, toX, fromY, toY);
        transAnim.setDuration(ms);
        return transAnim;
    }

    public static int calculatePopUpHeight(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static BoundingBoxE6 boundToMap(double minLatitude , double maxLatitude ,
                                    double minLongitude, double maxLongitude){
        double minLat = minLatitude;
        double maxLat = maxLatitude;
        double minLong = minLongitude;
        double maxLong = maxLongitude;
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (OverlayItem item : items) {
            IGeoPoint point = item.getPoint();
            if (point.getLatitudeE6() < minLat)
                minLat = point.getLatitudeE6();
            if (point.getLatitudeE6() > maxLat)
                maxLat = point.getLatitudeE6();
            if (point.getLongitudeE6() < minLong)
                minLong = point.getLongitudeE6();
            if (point.getLongitudeE6() > maxLong)
                maxLong = point.getLongitudeE6();
        }

        return new BoundingBoxE6(maxLat, maxLong, minLat, minLong);
    }

    public static String trimTWoDecimalPlaces(double value){
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(value);
    }

    public static JSONObject convertXMLtoJSON(String xmlString){
        JSONObject jsonObj=null;
        try {
            XmlToJson xmlToJson = new XmlToJson.Builder(xmlString).build();
            jsonObj = xmlToJson.toJson();
            Log.d("XML", xmlString);
            if (jsonObj != null) {
                Log.d("JSON", jsonObj.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonObj;
    }
}
