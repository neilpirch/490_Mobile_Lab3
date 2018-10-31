package com.neilpirch.socialloginapp;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.Callable;

public class Application {
    private static Application ourInstance = new Application();

    public static Application getInstance() {
        return ourInstance;
    }

    private Callable<Void> myLogoutCallable;

    private Application() {
    }

    public void setLogoutCallable(Callable<Void> callable) {
        myLogoutCallable = callable;
    }
}
