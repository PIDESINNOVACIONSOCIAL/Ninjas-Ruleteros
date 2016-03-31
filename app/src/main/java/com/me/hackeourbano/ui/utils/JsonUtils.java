package com.me.hackeourbano.ui.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Condesa on 08/03/16.
 */
public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    public static String getStringJsonObjectFromAsset(Context context, String file) {
        String filePath = String.format("json/%s.json", file);
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open(filePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getStringJsonArrayFromAsset(Context context, String file) {
        String filePath = String.format("json/%s.json", file);
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open(filePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
