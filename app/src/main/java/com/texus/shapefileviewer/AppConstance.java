package com.texus.shapefileviewer;

import android.graphics.Color;
import android.os.Environment;

/**
 * Created by sandeep on 12/2/16.
 */
public class AppConstance {
    public static final boolean D = true;

    public static final int COLOR_DEFAULT = Color.RED;
    public static final int COLOR_SELECTED = Color.parseColor("#550000FF");

    public static int zoomLevel = 10;

    public static String FILE_BASE_PATH = "";

    /*
    Invalid value returns by method if that method didnt get sufficient parameter
     */
    public static final long INVALID_VALUE = -1;

    public static void loadConfiguration() {
        FILE_BASE_PATH = Environment.getDataDirectory().getAbsolutePath();
        FILE_BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
