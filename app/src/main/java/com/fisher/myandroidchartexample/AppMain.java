package com.fisher.myandroidchartexample;

import android.app.Application;

public class AppMain extends Application {

    private static AppMain app = null;

    public AppMain() {
        super();
        app = this;
    }

    public static AppMain getApp() {
        return app;
    }
}
