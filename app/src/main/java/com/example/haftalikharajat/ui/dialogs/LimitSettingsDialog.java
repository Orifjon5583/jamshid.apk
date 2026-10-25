package com.example.haftalikharajat.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.db.AppRepository;
import com.example.haftalikharajat.data.model.ExpenseLimit;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.google.android.material.button.MaterialButton;

public class LimitSettingsDialog extends Dialog {

    private final AppRepository repository;
    private final OnLimitUpdatedListener onLimitUpdatedListener;

    public interface OnLimitUpdatedListener {
        void onLimitUpdated();
    }

    public LimitSettingsDialog(@NonNull Context context, OnLimitUpdatedListener listener) {
        super(context);
        this.repository = AppRepository.getInstance(context);
        this.onLimitUpdatedListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_limit_settings);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        RadioGroup rgType = findViewById(R.id.rgLimitType);
        RadioButton rbDaily = findViewById(R.id.rbLimitDaily);
        RadioButton rbWeekly = findViewById(R.id.rbLimitWeekly);
        RadioButton rbMonthly = findViewById(R.id.rbLimitMonthly);
        EditText etAmount = findViewById(R.id.etLimitAmount);
        SwitchCompat swEnable = findViewById(R.id.swLimitEnable);
        MaterialButton btnSave = findViewById(R.id.btnLimitSave);
        MaterialButton btnCancel = findViewById(R.id.btnLimitCancel);

        // Load existing weekly limit by default
        ExpenseLimit currentLimit = repository.getExpenseLimit(ExpenseLimit.TYPE_WEEKLY);
        if (currentLimit != null) {
            etAmount.setText(String.valueOf(Math.round(currentLimit.getAmount())));
            swEnable.setChecked(currentLimit.isEnabled());
        }

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedType = ExpenseLimit.TYPE_WEEKLY;
            if (checkedId == R.id.rbLimitDaily) {
                selectedType = ExpenseLimit.TYPE_DAILY;
            } else if (checkedId == R.id.rbLimitMonthly) {
                selectedType = ExpenseLimit.TYPE_MONTHLY;
            }

            ExpenseLimit lim = repository.getExpenseLimit(selectedType);
            if (lim != null) {
                etAmount.setText(String.valueOf(Math.round(lim.getAmount())));
                swEnable.setChecked(lim.isEnabled());
            } else {
                etAmount.setText("");
                swEnable.setChecked(true);
            }
        });

        btnSave.setOnClickListener(v -> {
            double amount = CurrencyUtils.parseAmount(etAmount.getText().toString());
            boolean enabled = swEnable.isChecked();

            String type = ExpenseLimit.TYPE_WEEKLY;
            if (rbDaily.isChecked()) {
                type = ExpenseLimit.TYPE_DAILY;
            } else if (rbMonthly.isChecked()) {
                type = ExpenseLimit.TYPE_MONTHLY;
            }

            if (amount <= 0 && enabled) {
                Toast.makeText(getContext(), R.string.err_invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            repository.setExpenseLimit(type, amount, enabled);
            Toast.makeText(getContext(), R.string.success_saved, Toast.LENGTH_SHORT).show();

            if (onLimitUpdatedListener != null) {
                onLimitUpdatedListener.onLimitUpdated();
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }
}
