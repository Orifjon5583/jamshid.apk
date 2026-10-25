package com.example.haftalikharajat;

import android.app.Application;

import com.example.haftalikharajat.data.db.AppRepository;
import com.example.haftalikharajat.utils.NotificationUtils;
import com.example.haftalikharajat.worker.BootReceiver;

public class HaftalikXarajatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize notifications channel
        NotificationUtils.createNotificationChannels(this);

        // Warm up database and seed initial data
        AppRepository.getInstance(this);

        // Schedule periodic debt check
        BootReceiver.schedulePeriodicWork(this);
    }
}
