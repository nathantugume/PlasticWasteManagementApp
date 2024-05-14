package com.ugwebstudio.plasticwastemanagementapp.classes;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class Scheduler {
    public static void schedulePickupCheck(Context context) {
        PeriodicWorkRequest pickupCheckRequest = new PeriodicWorkRequest.Builder(PickupReminderWorker.class, 1, TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "pickupReminder",
                ExistingPeriodicWorkPolicy.KEEP, // This will keep the existing periodic work if already scheduled
                pickupCheckRequest
        );
    }
}
