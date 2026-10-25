package com.example.haftalikharajat.ui.income;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.db.AppRepository;
import com.example.haftalikharajat.data.model.Transaction;
import com.example.haftalikharajat.ui.dialogs.EditTransactionDialog;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.example.haftalikharajat.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeFragment extends Fragment {

    private AppRepository repository;
    private IncomeAdapter adapter;

    private TextView tvMonthlyTotalIncome;
    private TextView tvIncomeCount;
    private TextView tvTopIncomeSource;

    private EditText etIncomeSource;
    private EditText etIncomeAmount;
    private TextView tvIncomeDate;
    private Spinner spIncomeAccount;
    private MaterialButton btnSaveIncome;
    private ImageButton btnIncomeFilter;

    private RecyclerView rvIncomeList;
    private TextView tvIncomeBadgeCount;
    private LinearLayout layoutIncomeEmpty;

    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = AppRepository.getInstance(requireContext());

        tvMonthlyTotalIncome = view.findViewById(R.id.tvMonthlyTotalIncome);
        tvIncomeCount = view.findViewById(R.id.tvIncomeCount);
        tvTopIncomeSource = view.findViewById(R.id.tvTopIncomeSource);

        etIncomeSource = view.findViewById(R.id.etIncomeSource);
        etIncomeAmount = view.findViewById(R.id.etIncomeAmount);
        tvIncomeDate = view.findViewById(R.id.tvIncomeDate);
        spIncomeAccount = view.findViewById(R.id.spIncomeAccount);
        btnSaveIncome = view.findViewById(R.id.btnSaveIncome);
        btnIncomeFilter = view.findViewById(R.id.btnIncomeFilter);

        rvIncomeList = view.findViewById(R.id.rvIncomeList);
        tvIncomeBadgeCount = view.findViewById(R.id.tvIncomeBadgeCount);
        layoutIncomeEmpty = view.findViewById(R.id.layoutIncomeEmpty);

        selectedDate = DateUtils.getTodayDate();
        tvIncomeDate.setText(selectedDate);

        // Account Spinner setup
        String[] accountOptions = {
                "Karta (Uzcard/Humo)",
                "Naqd pul",
                "Bank depoziti",
                "Payme / Click",
                "Boshqa"
        };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                accountOptions
        );
        spIncomeAccount.setAdapter(spinnerAdapter);

        // RecyclerView setup
        adapter = new IncomeAdapter(new IncomeAdapter.OnIncomeActionListener() {
            @Override
            public void onEdit(Transaction transaction) {
                showEditIncomeDialog(transaction);
            }

            @Override
            public void onDelete(Transaction transaction) {
                showDeleteConfirmDialog(transaction);
            }
        });
        rvIncomeList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIncomeList.setAdapter(adapter);

        // Date picker
        tvIncomeDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.parseDate(selectedDate));
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (dpView, year, month, dayOfMonth) -> {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                selectedDate = DateUtils.formatDate(c.getTime());
                tvIncomeDate.setText(selectedDate);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        // Save income
        btnSaveIncome.setOnClickListener(v -> saveNewIncome());

        btnIncomeFilter.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Barcha daromadlar saralangan", Toast.LENGTH_SHORT).show();
        });

        loadIncomeData();
    }

    private void saveNewIncome() {
        String source = etIncomeSource.getText().toString().trim();
        double amount = CurrencyUtils.parseAmount(etIncomeAmount.getText().toString());

        if (source.isEmpty()) {
            Toast.makeText(getContext(), R.string.err_empty_title, Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(getContext(), R.string.err_invalid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        String accountType = spIncomeAccount.getSelectedItem().toString();

        Transaction transaction = new Transaction(
                0,
                Transaction.TYPE_INCOME,
                source,
                amount,
                "Daromad",
                selectedDate,
                DateUtils.getCurrentTime(),
                accountType,
                "",
                System.currentTimeMillis()
        );

        repository.insertTransaction(transaction);

        // Reset inputs
        etIncomeSource.setText("");
        etIncomeAmount.setText("");
        selectedDate = DateUtils.getTodayDate();
        tvIncomeDate.setText(selectedDate);

        Toast.makeText(getContext(), R.string.success_saved, Toast.LENGTH_SHORT).show();

        loadIncomeData();
    }

    private void showEditIncomeDialog(Transaction item) {
        EditTransactionDialog dialog = new EditTransactionDialog(
                requireContext(),
                item,
                updated -> {
                    repository.updateTransaction(updated);
                    loadIncomeData();
                },
                deleted -> {
                    repository.deleteTransaction(deleted.getId());
                    loadIncomeData();
                }
        );
        dialog.show();
    }

    private void showDeleteConfirmDialog(Transaction item) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_desc)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    repository.deleteTransaction(item.getId());
                    loadIncomeData();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void loadIncomeData() {
        if (!isAdded()) return;

        Calendar cal = Calendar.getInstance();
        String startMonth = DateUtils.getStartOfMonth(cal);
        String endMonth = DateUtils.getEndOfMonth(cal);

        // Incomes for current month
        List<Transaction> incomes = repository.getIncomes(startMonth, endMonth);
        adapter.setItems(incomes);

        double totalMonthlyIncome = repository.getTotalAmount(Transaction.TYPE_INCOME, startMonth, endMonth);
        tvMonthlyTotalIncome.setText(CurrencyUtils.formatWithSign(totalMonthlyIncome, false));
        tvIncomeCount.setText(incomes.size() + " ta kirim");
        tvIncomeBadgeCount.setText(incomes.size() + " TA KIRIM");

        // Top source
        String topSource = "—";
        double maxSourceAmount = 0;
        Map<String, Double> sourceMap = new HashMap<>();
        for (Transaction t : incomes) {
            double cur = sourceMap.containsKey(t.getTitle()) ? sourceMap.get(t.getTitle()) : 0;
            double updated = cur + t.getAmount();
            sourceMap.put(t.getTitle(), updated);
            if (updated > maxSourceAmount) {
                maxSourceAmount = updated;
                topSource = t.getTitle();
            }
        }
        tvTopIncomeSource.setText(topSource);

        if (incomes.isEmpty()) {
            layoutIncomeEmpty.setVisibility(View.VISIBLE);
            rvIncomeList.setVisibility(View.GONE);
        } else {
            layoutIncomeEmpty.setVisibility(View.GONE);
            rvIncomeList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIncomeData();
    }
}
