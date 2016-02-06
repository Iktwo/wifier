package com.iktwo.wifier;

import android.app.Application;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class WiFierApplication extends Application {
    private static final String TAG = WiFierApplication.class.getSimpleName();

    public static HashMap<String, String> manufacturers;

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        manufacturers = new HashMap<>();
        InputStream inputStream = this.getResources().openRawResource(R.raw.oui);


        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;

        try {
            while ((line = buffreader.readLine()) != null) {

                if (line.length() > 9)
                    manufacturers.put(line.substring(0, 8), line.substring(9));
            }
        } catch (IOException e) {
            Log.e(TAG, "error");
        }
    }
}
