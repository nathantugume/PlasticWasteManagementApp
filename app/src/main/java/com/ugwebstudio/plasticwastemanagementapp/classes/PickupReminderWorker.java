package com.ugwebstudio.plasticwastemanagementapp.classes;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.plasticwastemanagementapp.R;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PickupReminderWorker extends Worker {
    private FirebaseFirestore db;
    private String date;
    private String time;


    public PickupReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        db = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public Result doWork() {
        checkPickups();
        return Result.success();
    }

    private void checkPickups() {
        db.collection("pickups")
                .whereEqualTo("notificationEnabled", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        compareDatesAndNotify(document);
                    }
                });
    }

    private void compareDatesAndNotify(DocumentSnapshot document) {
        String scheduledDate = document.getString("date");
                date = document.getString("date");
                time = document.getString("time");
        if (isTomorrow(scheduledDate)) {
            String userId = document.getString("userId");
            fetchUserAndNotify(userId);
        }
    }

    private boolean isTomorrow(String scheduledDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Adjust this format to match your date format
        try {
            Date date = sdf.parse(scheduledDate);
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            Calendar scheduled = Calendar.getInstance();
            scheduled.setTime(date);

            // Zero out the hour, minute, second, and millisecond
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);

            scheduled.set(Calendar.HOUR_OF_DAY, 0);
            scheduled.set(Calendar.MINUTE, 0);
            scheduled.set(Calendar.SECOND, 0);
            scheduled.set(Calendar.MILLISECOND, 0);

            return tomorrow.equals(scheduled);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fetchUserAndNotify(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(userDocument -> {
                    String phone = userDocument.getString("phone");
                    String name = userDocument.getString("fullName");
                    String message = "\"Hello, "+name+ " we are kindly reminding you that you scheduled a plastic waste collection for tomorrow "+date+ ", at " +time;

                    sendNotification(phone,message);
                    Context context = getApplicationContext();
                    notifyUserInApp(context,message);
                });
    }

    private void sendNotification(String phone, String message) {
        // Implementation to send SMS or app notification
        SmsSender.sendSms(phone,message );
    }


    private void notifyUserInApp(Context context, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "pickup_reminder";
        String channelName = "Pickup Reminder";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.waste_collection_logo) // Ensure you have a drawable resource for notification icon
                .setContentTitle("Pickup Reminder")
                .setContentText(message)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }


}
