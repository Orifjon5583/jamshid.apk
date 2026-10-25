package com.example.haftalikharajat.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.model.Transaction;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.example.haftalikharajat.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class EditTransactionDialog extends Dialog {

    private final Transaction transaction;
    private final OnSaveListener onSaveListener;
    private final OnDeleteListener onDeleteListener;

    public interface OnSaveListener {
        void onSave(Transaction transaction);
    }

    public interface OnDeleteListener {
        void onDelete(Transaction transaction);
    }

    public EditTransactionDialog(@NonNull Context context, Transaction transaction,
                                 OnSaveListener onSaveListener, OnDeleteListener onDeleteListener) {
        super(context);
        this.transaction = transaction;
        this.onSaveListener = onSaveListener;
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_transaction);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvTitle = findViewById(R.id.tvDialogTitle);
        EditText etTitle = findViewById(R.id.etDialogTitle);
        EditText etAmount = findViewById(R.id.etDialogAmount);
        TextView tvDate = findViewById(R.id.tvDialogDate);
        MaterialButton btnSave = findViewById(R.id.btnDialogSave);
        MaterialButton btnDelete = findViewById(R.id.btnDialogDelete);

        boolean isExpense = transaction.isExpense();
        tvTitle.setText(isExpense ? "Xarajatni tahrirlash" : "Daromadni tahrirlash");
        etTitle.setText(transaction.getTitle());
        etAmount.setText(String.valueOf(Math.round(transaction.getAmount())));
        tvDate.setText(DateUtils.formatUzbekDate(transaction.getDate()));

        final String[] selectedDate = {transaction.getDate()};

        tvDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.parseDate(selectedDate[0]));
            DatePickerDialog dpd = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                selectedDate[0] = DateUtils.formatDate(c.getTime());
                tvDate.setText(DateUtils.formatUzbekDate(selectedDate[0]));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            double amount = CurrencyUtils.parseAmount(etAmount.getText().toString());

            if (title.isEmpty()) {
                Toast.makeText(getContext(), R.string.err_empty_title, Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount <= 0) {
                Toast.makeText(getContext(), R.string.err_invalid_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            transaction.setTitle(title);
            transaction.setAmount(amount);
            transaction.setDate(selectedDate[0]);

            if (onSaveListener != null) {
                onSaveListener.onSave(transaction);
            }
            dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            if (onDeleteListener != null) {
                onDeleteListener.onDelete(transaction);
            }
            dismiss();
        });
    }
}
