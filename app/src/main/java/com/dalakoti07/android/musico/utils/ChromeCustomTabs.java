package com.dalakoti07.android.musico.utils;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.core.content.ContextCompat;

import com.dalakoti07.android.musico.MusicApplication;
import com.dalakoti07.android.musico.R;

/**
 * A Chrome Custom Tabs wrapper to preload and show URLs in Chrome Custom Tabs.
 * Also provides a static method to launch a tab directly without warm up.
 */
public class ChromeCustomTabs {

    private CustomTabsServiceConnection serviceConnection;
    private CustomTabsIntent mCustomTabsIntent;

    @ColorInt
    private int toolbarColor;
    private Context context;

    public ChromeCustomTabs(@NonNull Context context) {
        this.context = context;
        toolbarColor = ContextCompat.getColor(context, R.color.black);
        initService();
    }

    private void initService() {

        serviceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                customTabsClient.warmup(0L);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // NO-OP
            }
        };

        // Bind the Chrome Custom Tabs service
        CustomTabsClient.bindCustomTabsService(context, MusicApplication.getApplicationPackageName(), serviceConnection);

        mCustomTabsIntent = new CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(toolbarColor)
                .build();
    }

    public void launchUrl(String Url) {
        mCustomTabsIntent.launchUrl(context, Uri.parse(Url));
    }

    /**
     * Allow the Chrome Custom Tabs service to disconnect and GC.
     */
    public void destroy() {
        context.unbindService(serviceConnection);
    }

    /**
     * Launches a Chrome Custom Tab without warmup / service.
     *
     * @param context The context - used for launching an Activity.
     * @param url     The URL to load.
     */
    public static void launchUrl(@NonNull Context context, @NonNull String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
