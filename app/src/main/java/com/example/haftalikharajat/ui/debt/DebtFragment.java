package com.example.haftalikharajat.ui.debt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.db.AppRepository;
import com.example.haftalikharajat.data.model.Debt;
import com.example.haftalikharajat.ui.dialogs.EditDebtDialog;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.example.haftalikharajat.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.List;

public class DebtFragment extends Fragment {

    private AppRepository repository;
    private DebtAdapter adapter;

    private TextView tvTotalLentAmount;
    private TextView tvLentCount;
    private TextView tvTotalBorrowedAmount;
    private TextView tvBorrowedCount;

    private EditText etDebtPersonName;
    private EditText etDebtPhone;
    private EditText etDebtAmount;
    private EditText etDebtReason;
    private TextView tvDebtGivenDate;
    private TextView tvDebtDueDate;
    private RadioGroup rgDebtType;
    private RadioButton rbDebtLent;
    private RadioButton rbDebtBorrowed;
    private MaterialButton btnSaveDebt;
    private ImageButton btnSearchDebts;

    private TextView btnTabActiveDebts;
    private TextView btnTabClosedDebts;
    private TextView tvDebtsCountBadge;
    private RecyclerView rvDebts;
    private TextView tvEmptyDebts;

    private String selectedGivenDate;
    private String selectedDueDate;
    private boolean showingActive = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = AppRepository.getInstance(requireContext());

        tvTotalLentAmount = view.findViewById(R.id.tvTotalLentAmount);
        tvLentCount = view.findViewById(R.id.tvLentCount);
        tvTotalBorrowedAmount = view.findViewById(R.id.tvTotalBorrowedAmount);
        tvBorrowedCount = view.findViewById(R.id.tvBorrowedCount);

        etDebtPersonName = view.findViewById(R.id.etDebtPersonName);
        etDebtPhone = view.findViewById(R.id.etDebtPhone);
        etDebtAmount = view.findViewById(R.id.etDebtAmount);
        etDebtReason = view.findViewById(R.id.etDebtReason);
        tvDebtGivenDate = view.findViewById(R.id.tvDebtGivenDate);
        tvDebtDueDate = view.findViewById(R.id.tvDebtDueDate);
        rgDebtType = view.findViewById(R.id.rgDebtType);
        rbDebtLent = view.findViewById(R.id.rbDebtLent);
        rbDebtBorrowed = view.findViewById(R.id.rbDebtBorrowed);
        btnSaveDebt = view.findViewById(R.id.btnSaveDebt);
        btnSearchDebts = view.findViewById(R.id.btnSearchDebts);

        btnTabActiveDebts = view.findViewById(R.id.btnTabActiveDebts);
        btnTabClosedDebts = view.findViewById(R.id.btnTabClosedDebts);
        tvDebtsCountBadge = view.findViewById(R.id.tvDebtsCountBadge);
        rvDebts = view.findViewById(R.id.rvDebts);
        tvEmptyDebts = view.findViewById(R.id.tvEmptyDebts);

        // Dates default
        selectedGivenDate = DateUtils.getTodayDate();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 14); // 2 weeks default due
        selectedDueDate = DateUtils.formatDate(cal.getTime());

        tvDebtGivenDate.setText(DateUtils.formatUzbekDate(selectedGivenDate));
        tvDebtDueDate.setText(DateUtils.formatUzbekDate(selectedDueDate));

        // Date pickers
        tvDebtGivenDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.setTime(DateUtils.parseDate(selectedGivenDate));
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (dpView, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                selectedGivenDate = DateUtils.formatDate(selected.getTime());
                tvDebtGivenDate.setText(DateUtils.formatUzbekDate(selectedGivenDate));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        tvDebtDueDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.setTime(DateUtils.parseDate(selectedDueDate));
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (dpView, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                selectedDueDate = DateUtils.formatDate(selected.getTime());
                tvDebtDueDate.setText(DateUtils.formatUzbekDate(selectedDueDate));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        // Setup RecyclerView
        adapter = new DebtAdapter(new DebtAdapter.OnDebtActionListener() {
            @Override
            public void onEdit(Debt debt) {
                showEditDebtDialog(debt);
            }

            @Override
            public void onDelete(Debt debt) {
                showDeleteConfirmDialog(debt);
            }

            @Override
            public void onMarkPaid(Debt debt) {
                repository.markDebtClosed(debt.getId(), DateUtils.getTodayDate());
                Toast.makeText(requireContext(), "Qarz to'landi deb belgilandi!", Toast.LENGTH_SHORT).show();
                loadDebtData();
            }
        });
        rvDebts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDebts.setAdapter(adapter);

        // Tabs
        btnTabActiveDebts.setOnClickListener(v -> {
            showingActive = true;
            updateTabStyles();
            loadDebtData();
        });

        btnTabClosedDebts.setOnClickListener(v -> {
            showingActive = false;
            updateTabStyles();
            loadDebtData();
        });

        // Save
        btnSaveDebt.setOnClickListener(v -> saveNewDebt());

        btnSearchDebts.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Qarzlar holati yangilandi", Toast.LENGTH_SHORT).show();
            loadDebtData();
        });

        updateTabStyles();
        loadDebtData();
    }

    private void updateTabStyles() {
        if (showingActive) {
            btnTabActiveDebts.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary));
            btnTabActiveDebts.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_primary));

            btnTabClosedDebts.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.surface_card_secondary));
            btnTabClosedDebts.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        } else {
            btnTabActiveDebts.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.surface_card_secondary));
            btnTabActiveDebts.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

            btnTabClosedDebts.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.primary));
            btnTabClosedDebts.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_primary));
        }
    }

    private void saveNewDebt() {
        String person = etDebtPersonName.getText().toString().trim();
        double amount = CurrencyUtils.parseAmount(etDebtAmount.getText().toString());
        String phone = etDebtPhone.getText().toString().trim();
        String reason = etDebtReason.getText().toString().trim();

        if (person.isEmpty()) {
            Toast.makeText(getContext(), R.string.err_empty_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(getContext(), R.string.err_invalid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDueDate.compareTo(selectedGivenDate) < 0) {
            Toast.makeText(getContext(), R.string.err_due_before_given, Toast.LENGTH_SHORT).show();
            return;
        }

        String type = rbDebtLent.isChecked() ? Debt.TYPE_LENT : Debt.TYPE_BORROWED;

        Debt debt = new Debt(
                0,
                person,
                amount,
                reason,
                phone,
                selectedGivenDate,
                selectedDueDate,
                type,
                Debt.STATUS_ACTIVE,
                null
        );

        repository.insertDebt(debt);

        // Reset
        etDebtPersonName.setText("");
        etDebtPhone.setText("");
        etDebtAmount.setText("");
        etDebtReason.setText("");

        Toast.makeText(getContext(), R.string.success_saved, Toast.LENGTH_SHORT).show();

        loadDebtData();
    }

    private void showEditDebtDialog(Debt debt) {
        EditDebtDialog dialog = new EditDebtDialog(
                requireContext(),
                debt,
                updated -> {
                    repository.updateDebt(updated);
                    loadDebtData();
                },
                deleted -> {
                    repository.deleteDebt(deleted.getId());
                    loadDebtData();
                }
        );
        dialog.show();
    }

    private void showDeleteConfirmDialog(Debt debt) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm_desc)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    repository.deleteDebt(debt.getId());
                    loadDebtData();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void loadDebtData() {
        if (!isAdded()) return;

        // Metric summaries
        double totalLent = repository.getTotalActiveDebtAmount(Debt.TYPE_LENT);
        int lentCount = repository.countActiveDebts(Debt.TYPE_LENT);
        tvTotalLentAmount.setText(CurrencyUtils.formatAmount(totalLent));
        tvLentCount.setText("Burch soni: " + lentCount + " ta");

        double totalBorrowed = repository.getTotalActiveDebtAmount(Debt.TYPE_BORROWED);
        int borrowedCount = repository.countActiveDebts(Debt.TYPE_BORROWED);
        tvTotalBorrowedAmount.setText(CurrencyUtils.formatAmount(totalBorrowed));
        tvBorrowedCount.setText("Kutilmoqda: " + borrowedCount + " ta");

        // List
        List<Debt> list = repository.getDebtsByStatus(showingActive ? Debt.STATUS_ACTIVE : Debt.STATUS_CLOSED);
        adapter.setItems(list);

        tvDebtsCountBadge.setText(list.size() + " ta tranzaksiya");
        tvEmptyDebts.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        rvDebts.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDebtData();
    }
}
