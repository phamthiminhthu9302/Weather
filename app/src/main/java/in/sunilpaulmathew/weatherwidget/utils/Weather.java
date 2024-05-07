package in.sunilpaulmathew.weatherwidget.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.utils.Utils;


public class Weather {
    public static String getLatitude(Context context) {
        return Utils.getString("latitude", null, context);
    }
    public static String getLongitude(Context context) {
        return Utils.getString("longitude", null, context);
    }




}