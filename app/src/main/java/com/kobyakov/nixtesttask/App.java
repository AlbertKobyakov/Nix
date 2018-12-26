package com.kobyakov.nixtesttask;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.kobyakov.nixtesttask.di.component.AppComponent;
import com.kobyakov.nixtesttask.di.component.DaggerAppComponent;
import com.kobyakov.nixtesttask.database.AppDatabase;

public class App extends Application {
    public static App INSTANCE;
    private static final String DATABASE_NAME = "Products.db";
    private AppDatabase database;
    private static AppComponent component;

    public static App get() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();

        INSTANCE = this;

        component = DaggerAppComponent.create();
    }

    public static AppComponent getComponent() {
        return component;
    }

    public AppDatabase getDB() {
        return database;
    }
}
