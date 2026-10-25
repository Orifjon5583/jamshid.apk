package com.example.haftalikharajat.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.model.Debt;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.example.haftalikharajat.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class EditDebtDialog extends Dialog {

    private final Debt debt;
    private final OnSaveListener onSaveListener;
    private final OnDeleteListener onDeleteListener;

    public interface OnSaveListener {
        void onSave(Debt debt);
    }

    public interface OnDeleteListener {
        void onDelete(Debt debt);
    }

    public EditDebtDialog(@NonNull Context context, Debt debt,
                          OnSaveListener onSaveListener, OnDeleteListener onDeleteListener) {
        super(context);
        this.debt = debt;
        this.onSaveListener = onSaveListener;
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_debt);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText etPerson = findViewById(R.id.etDialogDebtPerson);
        EditText etAmount = findViewById(R.id.etDialogDebtAmount);
        EditText etPhone = findViewById(R.id.etDialogDebtPhone);
        EditText etReason = findViewById(R.id.etDialogDebtReason);
        TextView tvDueDate = findViewById(R.id.tvDialogDebtDueDate);
        RadioButton rbLent = findViewById(R.id.rbDialogDebtLent);
        RadioButton rbBorrowed = findViewById(R.id.rbDialogDebtBorrowed);
        MaterialButton btnSave = findViewById(R.id.btnDialogDebtSave);
        MaterialButton btnDelete = findViewById(R.id.btnDialogDebtDelete);

        etPerson.setText(debt.getPersonName());
        etAmount.setText(String.valueOf(Math.round(debt.getAmount())));
        etPhone.setText(debt.getPhone() != null ? debt.getPhone() : "");
        etReason.setText(debt.getReason() != null ? debt.getReason() : "");
        tvDueDate.setText(DateUtils.formatUzbekDate(debt.getDueDate()));

        if (debt.isLent()) {
            rbLent.setChecked(true);
        } else {
            rbBorrowed.setChecked(true);
        }

        final String[] selectedDueDate = {debt.getDueDate()};

        tvDueDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.parseDate(selectedDueDate[0]));
            DatePickerDialog dpd = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                selectedDueDate[0] = DateUtils.formatDate(c.getTime());
                tvDueDate.setText(DateUtils.formatUzbekDate(selectedDueDate[0]));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnSave.setOnClickListener(v -> {
            String person = etPerson.getText().toString().trim();
            double amount = CurrencyUtils.parseAmount(etAmount.getText().toString());
            String phone = etPhone.getText().toString().trim();
            String reason = etReason.getText().toString().trim();

            if (person.isEmpty()) {
                Toast.makeText(getContext(), R.string.err_empty_name, Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount <= 0) {
                Toast.makeText(getContext(), R.string.err_invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            debt.setPersonName(person);
            debt.setAmount(amount);
            debt.setPhone(phone);
            debt.setReason(reason);
            debt.setDueDate(selectedDueDate[0]);
            debt.setType(rbLent.isChecked() ? Debt.TYPE_LENT : Debt.TYPE_BORROWED);

            if (onSaveListener != null) {
                onSaveListener.onSave(debt);
            }
            dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            if (onDeleteListener != null) {
                onDeleteListener.onDelete(debt);
            }
            dismiss();
        });
    }
}
