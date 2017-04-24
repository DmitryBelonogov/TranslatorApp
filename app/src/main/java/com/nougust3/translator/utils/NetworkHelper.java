package com.nougust3.translator.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nougust3.translator.Translator;

public class NetworkHelper {

    private static NetworkHelper networkHelper;

    public static synchronized NetworkHelper getInstance() {
        if (networkHelper == null)
            networkHelper = new NetworkHelper();
        return networkHelper;
    }

    public boolean isNetworkAvailable() {
        NetworkInfo activeNetwork =
                ((ConnectivityManager) Translator.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE))
                        .getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
