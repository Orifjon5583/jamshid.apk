package com.example.haftalikharajat.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.haftalikharajat.MainActivity;
import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.model.Debt;

public class NotificationUtils {

    public static final String CHANNEL_ID_DEBT = "debt_reminders";
    private static final String CHANNEL_NAME_DEBT = "Qarz eslatmalari";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID_DEBT,
                    CHANNEL_NAME_DEBT,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Qarz muddatlari va eslatmalari");
            channel.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showDebtNotification(Context context, Debt debt, int notificationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("target_tab", R.id.nav_debt);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String typeDesc = debt.isLent() ? "Sizdan olingan qarz" : "Siz berishingiz kerak bo'lgan qarz";
        String content = debt.getPersonName() + " (" + CurrencyUtils.formatAmount(debt.getAmount()) + ") - " + typeDesc;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_DEBT)
                .setSmallIcon(R.drawable.ic_nav_debt)
                .setContentTitle("Qarz muddati eslatmasi!")
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content + "\nIzoh: " + (debt.getReason() != null ? debt.getReason() : "")))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }
}
