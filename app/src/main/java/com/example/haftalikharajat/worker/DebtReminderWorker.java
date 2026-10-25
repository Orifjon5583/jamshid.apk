package com.example.haftalikharajat.worker;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.haftalikharajat.data.db.AppRepository;
import com.example.haftalikharajat.data.model.Debt;
import com.example.haftalikharajat.utils.DateUtils;
import com.example.haftalikharajat.utils.NotificationUtils;

import java.util.List;

public class DebtReminderWorker extends Worker {

    private static final String PREFS_REMINDERS = "debt_reminders_prefs";

    public DebtReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        AppRepository repository = AppRepository.getInstance(context);
        List<Debt> dueDebts = repository.getDueDebtsForNotification();

        if (dueDebts.isEmpty()) {
            return Result.success();
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_REMINDERS, Context.MODE_PRIVATE);
        String today = DateUtils.getTodayDate();

        for (Debt debt : dueDebts) {
            String key = "notif_" + debt.getId() + "_" + today;
            // Prevent multiple notifications per debt per day
            if (!prefs.getBoolean(key, false)) {
                NotificationUtils.showDebtNotification(context, debt, (int) debt.getId());
                prefs.edit().putBoolean(key, true).apply();
            }
        }

        return Result.success();
    }
}
