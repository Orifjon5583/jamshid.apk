package com.example.haftalikharajat.ui.expense;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haftalikharajat.MainActivity;
import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.db.AppRepository;
import com.example.haftalikharajat.data.model.ExpenseLimit;
import com.example.haftalikharajat.data.model.Transaction;
import com.example.haftalikharajat.ui.dialogs.EditTransactionDialog;
import com.example.haftalikharajat.ui.dialogs.LimitSettingsDialog;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.example.haftalikharajat.utils.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseFragment extends Fragment {

    private static final String PREF_HIDE_BALANCE = "hide_balance";

    private AppRepository repository;
    private ExpenseAdapter adapter;

    private TextView tvHeaderDate;
    private TextView tvBalanceTag;
    private TextView tvTotalBalance;
    private TextView tvHeaderIncome;
    private TextView tvHeaderExpense;
    private ImageButton btnToggleBalance;
    private ImageButton btnSettings;
    private ImageButton btnNotification;

    private LinearLayout layoutLimitBanner;
    private TextView tvLimitText;
    private TextView tvLimitPercent;
    private ProgressBar pbExpenseLimit;

    private EditText etExpenseTitle;
    private EditText etExpenseAmount;
    private TextView tvExpenseDate;
    private TextView tvExpenseTime;
    private ChipGroup cgExpenseCategories;
    private MaterialButton btnSaveExpense;

    private RecyclerView rvRecentExpenses;
    private TextView tvEmptyExpenses;
    private TextView tvSeeAllExpenses;

    private String selectedDate;
    private String selectedTime;
    private boolean isBalanceHidden = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = AppRepository.getInstance(requireContext());

        // Initialize Views
        tvHeaderDate = view.findViewById(R.id.tvHeaderDate);
        tvBalanceTag = view.findViewById(R.id.tvBalanceTag);
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvHeaderIncome = view.findViewById(R.id.tvHeaderIncome);
        tvHeaderExpense = view.findViewById(R.id.tvHeaderExpense);
        btnToggleBalance = view.findViewById(R.id.btnToggleBalance);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnNotification = view.findViewById(R.id.btnNotification);

        layoutLimitBanner = view.findViewById(R.id.layoutLimitBanner);
        tvLimitText = view.findViewById(R.id.tvLimitText);
        tvLimitPercent = view.findViewById(R.id.tvLimitPercent);
        pbExpenseLimit = view.findViewById(R.id.pbExpenseLimit);

        etExpenseTitle = view.findViewById(R.id.etExpenseTitle);
        etExpenseAmount = view.findViewById(R.id.etExpenseAmount);
        tvExpenseDate = view.findViewById(R.id.tvExpenseDate);
        tvExpenseTime = view.findViewById(R.id.tvExpenseTime);
        cgExpenseCategories = view.findViewById(R.id.cgExpenseCategories);
        btnSaveExpense = view.findViewById(R.id.btnSaveExpense);

        rvRecentExpenses = view.findViewById(R.id.rvRecentExpenses);
        tvEmptyExpenses = view.findViewById(R.id.tvEmptyExpenses);
        tvSeeAllExpenses = view.findViewById(R.id.tvSeeAllExpenses);

        // Date and Time setup
        selectedDate = DateUtils.getTodayDate();
        selectedTime = DateUtils.getCurrentTime();
        tvExpenseDate.setText(selectedDate);
        tvExpenseTime.setText(selectedTime);

        // Header date
        tvHeaderDate.setText(DateUtils.formatHeaderDate(new Date()));
        int weekNumber = DateUtils.getWeekOfYear(new Date());
        tvBalanceTag.setText("JAMI BALANS HAFTA " + weekNumber);

        // Setup RecyclerView
        adapter = new ExpenseAdapter(this::showEditExpenseDialog);
        rvRecentExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentExpenses.setAdapter(adapter);

        // Balance hide/show state
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        isBalanceHidden = prefs.getBoolean(PREF_HIDE_BALANCE, false);

        btnToggleBalance.setOnClickListener(v -> {
            isBalanceHidden = !isBalanceHidden;
            prefs.edit().putBoolean(PREF_HIDE_BALANCE, isBalanceHidden).apply();
            updateBalanceViews();
        });

        // Date Picker
        tvExpenseDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.parseDate(selectedDate));
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (dpView, year, month, dayOfMonth) -> {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                selectedDate = DateUtils.formatDate(c.getTime());
                tvExpenseDate.setText(selectedDate);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        // Time Picker
        tvExpenseTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            TimePickerDialog tpd = new TimePickerDialog(requireContext(), (tpView, hourOfDay, minute) -> {
                selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                tvExpenseTime.setText(selectedTime);
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            tpd.show();
        });

        // Settings / Limits Dialog
        btnSettings.setOnClickListener(v -> {
            LimitSettingsDialog dialog = new LimitSettingsDialog(requireContext(), this::loadData);
            dialog.show();
        });

        btnNotification.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Bildirishnomalar tizimi faol", Toast.LENGTH_SHORT).show();
        });

        // Navigate to History Tab
        tvSeeAllExpenses.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTab(R.id.nav_history);
            }
        });

        // Save Expense Button
        btnSaveExpense.setOnClickListener(v -> saveNewExpense());

        loadData();
    }

    private void saveNewExpense() {
        String title = etExpenseTitle.getText().toString().trim();
        double amount = CurrencyUtils.parseAmount(etExpenseAmount.getText().toString());

        if (title.isEmpty()) {
            Toast.makeText(getContext(), R.string.err_empty_title, Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(getContext(), R.string.err_invalid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        String category = "Oziq-ovqat";
        int checkedChipId = cgExpenseCategories.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip chip = cgExpenseCategories.findViewById(checkedChipId);
            if (chip != null) {
                category = chip.getText().toString();
            }
        }

        Transaction transaction = new Transaction(
                0,
                Transaction.TYPE_EXPENSE,
                title,
                amount,
                category,
                selectedDate,
                selectedTime,
                "Karta",
                "",
                System.currentTimeMillis()
        );

        repository.insertTransaction(transaction);

        // Reset form
        etExpenseTitle.setText("");
        etExpenseAmount.setText("");
        selectedDate = DateUtils.getTodayDate();
        selectedTime = DateUtils.getCurrentTime();
        tvExpenseDate.setText(selectedDate);
        tvExpenseTime.setText(selectedTime);

        Toast.makeText(getContext(), R.string.success_saved, Toast.LENGTH_SHORT).show();

        loadData();
    }

    private void showEditExpenseDialog(Transaction item) {
        EditTransactionDialog dialog = new EditTransactionDialog(
                requireContext(),
                item,
                updated -> {
                    repository.updateTransaction(updated);
                    loadData();
                },
                deleted -> {
                    repository.deleteTransaction(deleted.getId());
                    loadData();
                }
        );
        dialog.show();
    }

    public void loadData() {
        if (!isAdded()) return;

        // Load metrics
        updateBalanceViews();

        // Load recent expenses
        List<Transaction> recent = repository.getRecentExpenses(10);
        adapter.setItems(recent);
        tvEmptyExpenses.setVisibility(recent.isEmpty() ? View.VISIBLE : View.GONE);
        rvRecentExpenses.setVisibility(recent.isEmpty() ? View.GONE : View.VISIBLE);

        // Check expense limits
        checkLimits();
    }

    private void updateBalanceViews() {
        double totalIncome = repository.getTotalAmount(Transaction.TYPE_INCOME, null, null);
        double totalExpense = repository.getTotalAmount(Transaction.TYPE_EXPENSE, null, null);
        double netBalance = totalIncome - totalExpense;

        if (isBalanceHidden) {
            tvTotalBalance.setText("••••••• so'm");
            tvHeaderIncome.setText("•••••••");
            tvHeaderExpense.setText("•••••••");
            btnToggleBalance.setImageResource(R.drawable.ic_eye_off);
        } else {
            tvTotalBalance.setText(CurrencyUtils.formatAmount(netBalance));
            tvHeaderIncome.setText(CurrencyUtils.formatWithSign(totalIncome, false));
            tvHeaderExpense.setText(CurrencyUtils.formatWithSign(totalExpense, true));
            btnToggleBalance.setImageResource(R.drawable.ic_eye);
        }
    }

    private void checkLimits() {
        ExpenseLimit weeklyLimit = repository.getExpenseLimit(ExpenseLimit.TYPE_WEEKLY);
        if (weeklyLimit != null && weeklyLimit.isEnabled() && weeklyLimit.getAmount() > 0) {
            Calendar cal = Calendar.getInstance();
            String startWeek = DateUtils.getStartOfWeek(cal);
            String endWeek = DateUtils.getEndOfWeek(cal);
            double weekExpenses = repository.getTotalAmount(Transaction.TYPE_EXPENSE, startWeek, endWeek);

            int pct = (int) Math.round((weekExpenses / weeklyLimit.getAmount()) * 100);
            layoutLimitBanner.setVisibility(View.VISIBLE);
            pbExpenseLimit.setProgress(Math.min(pct, 100));
            tvLimitPercent.setText(pct + "%");

            if (pct >= 100) {
                tvLimitText.setText(R.string.limit_exceeded);
                tvLimitText.setTextColor(getResources().getColor(R.color.expense));
                tvLimitPercent.setTextColor(getResources().getColor(R.color.expense));
            } else if (pct >= 80) {
                tvLimitText.setText(R.string.limit_warning);
                tvLimitText.setTextColor(getResources().getColor(R.color.warning_text));
                tvLimitPercent.setTextColor(getResources().getColor(R.color.warning_text));
            } else {
                tvLimitText.setText("Haftalik limit: " + CurrencyUtils.formatAmount(weeklyLimit.getAmount()));
                tvLimitText.setTextColor(getResources().getColor(R.color.text_primary));
                tvLimitPercent.setTextColor(getResources().getColor(R.color.primary));
            }
        } else {
            layoutLimitBanner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
