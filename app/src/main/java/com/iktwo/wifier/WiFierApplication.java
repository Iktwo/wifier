package com.iktwo.wifier;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
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

        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

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
