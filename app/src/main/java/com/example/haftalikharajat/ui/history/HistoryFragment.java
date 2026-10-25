package com.example.haftalikharajat.ui.history;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.db.AppRepository;
import com.example.haftalikharajat.data.model.CategoryStat;
import com.example.haftalikharajat.data.model.DayStat;
import com.example.haftalikharajat.data.model.Transaction;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.example.haftalikharajat.utils.DateUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final int PERIOD_DAILY = 0;
    private static final int PERIOD_WEEKLY = 1;
    private static final int PERIOD_MONTHLY = 2;
    private static final int PERIOD_YEARLY = 3;

    private int currentPeriod = PERIOD_WEEKLY;

    private AppRepository repository;
    private CategoryBreakdownAdapter categoryAdapter;
    private DailyGroupedAdapter operationsAdapter;

    private TextView tabDaily;
    private TextView tabWeekly;
    private TextView tabMonthly;
    private TextView tabYearly;

    private TextView tvDateRangeDesc;
    private TextView btnResetRange;
    private ImageButton btnHistoryDateFilter;

    private TextView tvTotalMetricLabel;
    private TextView tvTotalMetricValue;
    private TextView tvAverageMetricLabel;
    private TextView tvAverageMetricValue;

    private MaterialCardView cardBarChart;
    private WeeklyBarChartView barChartView;

    private RecyclerView rvCategoryBreakdown;
    private TextView tvEmptyCategories;

    private RecyclerView rvDailyOperations;
    private TextView tvEmptyOperations;
    private TextView tvOperationsCountBadge;

    private Calendar activeCalendar;
    private String customStartDate = null;
    private String customEndDate = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = AppRepository.getInstance(requireContext());
        activeCalendar = Calendar.getInstance();

        tabDaily = view.findViewById(R.id.tabDaily);
        tabWeekly = view.findViewById(R.id.tabWeekly);
        tabMonthly = view.findViewById(R.id.tabMonthly);
        tabYearly = view.findViewById(R.id.tabYearly);

        tvDateRangeDesc = view.findViewById(R.id.tvDateRangeDesc);
        btnResetRange = view.findViewById(R.id.btnResetRange);
        btnHistoryDateFilter = view.findViewById(R.id.btnHistoryDateFilter);

        tvTotalMetricLabel = view.findViewById(R.id.tvTotalMetricLabel);
        tvTotalMetricValue = view.findViewById(R.id.tvTotalMetricValue);
        tvAverageMetricLabel = view.findViewById(R.id.tvAverageMetricLabel);
        tvAverageMetricValue = view.findViewById(R.id.tvAverageMetricValue);

        cardBarChart = view.findViewById(R.id.cardBarChart);
        barChartView = view.findViewById(R.id.barChartView);

        rvCategoryBreakdown = view.findViewById(R.id.rvCategoryBreakdown);
        tvEmptyCategories = view.findViewById(R.id.tvEmptyCategories);

        rvDailyOperations = view.findViewById(R.id.rvDailyOperations);
        tvEmptyOperations = view.findViewById(R.id.tvEmptyOperations);
        tvOperationsCountBadge = view.findViewById(R.id.tvOperationsCountBadge);

        // Setup Adapters
        categoryAdapter = new CategoryBreakdownAdapter();
        rvCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategoryBreakdown.setAdapter(categoryAdapter);

        operationsAdapter = new DailyGroupedAdapter();
        rvDailyOperations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDailyOperations.setAdapter(operationsAdapter);

        // Tab click listeners
        tabDaily.setOnClickListener(v -> setPeriod(PERIOD_DAILY));
        tabWeekly.setOnClickListener(v -> setPeriod(PERIOD_WEEKLY));
        tabMonthly.setOnClickListener(v -> setPeriod(PERIOD_MONTHLY));
        tabYearly.setOnClickListener(v -> setPeriod(PERIOD_YEARLY));

        // Date picker filter
        btnHistoryDateFilter.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (dpView, year, month, dayOfMonth) -> {
                activeCalendar.set(year, month, dayOfMonth);
                customStartDate = null;
                customEndDate = null;
                loadHistoryData();
            }, activeCalendar.get(Calendar.YEAR), activeCalendar.get(Calendar.MONTH), activeCalendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        // Reset filter
        btnResetRange.setOnClickListener(v -> {
            activeCalendar = Calendar.getInstance();
            customStartDate = null;
            customEndDate = null;
            loadHistoryData();
        });

        // Bar chart tap
        barChartView.setOnDaySelectedListener((dayStat, index) -> {
            // Filter operations for tapped day
            customStartDate = dayStat.getDate();
            customEndDate = dayStat.getDate();
            tvDateRangeDesc.setText("Tanlangan kun: " + DateUtils.formatUzbekDate(dayStat.getDate()));
            loadDataForRange(customStartDate, customEndDate);
        });

        setPeriod(PERIOD_WEEKLY);
    }

    private void setPeriod(int period) {
        currentPeriod = period;
        customStartDate = null;
        customEndDate = null;

        // Update tab styles
        updateTabButton(tabDaily, period == PERIOD_DAILY);
        updateTabButton(tabWeekly, period == PERIOD_WEEKLY);
        updateTabButton(tabMonthly, period == PERIOD_MONTHLY);
        updateTabButton(tabYearly, period == PERIOD_YEARLY);

        // Bar chart only visible on Weekly tab
        cardBarChart.setVisibility(period == PERIOD_WEEKLY ? View.VISIBLE : View.GONE);

        loadHistoryData();
    }

    private void updateTabButton(TextView tab, boolean isSelected) {
        if (isSelected) {
            tab.setBackgroundResource(R.drawable.bg_tab_selected);
            tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_primary));
        } else {
            tab.setBackgroundResource(R.drawable.bg_tab_unselected);
            tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        }
    }

    public void loadHistoryData() {
        if (!isAdded()) return;

        String startDate;
        String endDate;

        if (customStartDate != null && customEndDate != null) {
            startDate = customStartDate;
            endDate = customEndDate;
        } else {
            switch (currentPeriod) {
                case PERIOD_DAILY:
                    startDate = DateUtils.formatDate(activeCalendar.getTime());
                    endDate = startDate;
                    tvDateRangeDesc.setText("Bugun: " + DateUtils.formatUzbekDate(startDate));
                    tvTotalMetricLabel.setText("KUNLIK JAMI");
                    tvAverageMetricLabel.setText("SOF QOLDIQ");
                    break;
                case PERIOD_WEEKLY:
                    startDate = DateUtils.getStartOfWeek(activeCalendar);
                    endDate = DateUtils.getEndOfWeek(activeCalendar);
                    tvDateRangeDesc.setText(DateUtils.formatUzbekDate(startDate) + " – " + DateUtils.formatUzbekDate(endDate));
                    tvTotalMetricLabel.setText("HAFTALIK JAMI");
                    tvAverageMetricLabel.setText("KUNLIK O'RTACHA");
                    break;
                case PERIOD_MONTHLY:
                    startDate = DateUtils.getStartOfMonth(activeCalendar);
                    endDate = DateUtils.getEndOfMonth(activeCalendar);
                    tvDateRangeDesc.setText("Oy: " + DateUtils.formatUzbekDate(startDate));
                    tvTotalMetricLabel.setText("OYLIK JAMI");
                    tvAverageMetricLabel.setText("KUNLIK O'RTACHA");
                    break;
                case PERIOD_YEARLY:
                default:
                    startDate = DateUtils.getStartOfYear(activeCalendar);
                    endDate = DateUtils.getEndOfYear(activeCalendar);
                    tvDateRangeDesc.setText("Yil: " + activeCalendar.get(Calendar.YEAR));
                    tvTotalMetricLabel.setText("YILLIK JAMI");
                    tvAverageMetricLabel.setText("OYLIK O'RTACHA");
                    break;
            }
        }

        loadDataForRange(startDate, endDate);
    }

    private void loadDataForRange(String startDate, String endDate) {
        double totalExpense = repository.getTotalAmount(Transaction.TYPE_EXPENSE, startDate, endDate);
        double totalIncome = repository.getTotalAmount(Transaction.TYPE_INCOME, startDate, endDate);

        tvTotalMetricValue.setText(CurrencyUtils.formatAmount(totalExpense));

        if (currentPeriod == PERIOD_DAILY) {
            double net = totalIncome - totalExpense;
            tvAverageMetricValue.setText(CurrencyUtils.formatAmount(net));
        } else if (currentPeriod == PERIOD_WEEKLY) {
            double dailyAvg = totalExpense / 7.0;
            tvAverageMetricValue.setText(CurrencyUtils.formatAmount(dailyAvg));

            // Load Weekly Bar chart data
            List<DayStat> dayStats = repository.getWeeklyDayStats(activeCalendar);
            barChartView.setDayStats(dayStats);
        } else if (currentPeriod == PERIOD_MONTHLY) {
            int maxDays = activeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            double dailyAvg = totalExpense / (double) Math.max(1, maxDays);
            tvAverageMetricValue.setText(CurrencyUtils.formatAmount(dailyAvg));
        } else {
            double monthlyAvg = totalExpense / 12.0;
            tvAverageMetricValue.setText(CurrencyUtils.formatAmount(monthlyAvg));
        }

        // Category breakdown
        List<CategoryStat> categoryStats = repository.getCategoryBreakdown(startDate, endDate);
        categoryAdapter.setItems(categoryStats);
        tvEmptyCategories.setVisibility(categoryStats.isEmpty() ? View.VISIBLE : View.GONE);
        rvCategoryBreakdown.setVisibility(categoryStats.isEmpty() ? View.GONE : View.VISIBLE);

        // Daily grouped operations
        List<Transaction> transactions = repository.getTransactions(null, startDate, endDate);
        operationsAdapter.setTransactions(transactions);
        tvEmptyOperations.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
        rvDailyOperations.setVisibility(transactions.isEmpty() ? View.GONE : View.VISIBLE);
        tvOperationsCountBadge.setText(transactions.size() + " ta operatsiya");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistoryData();
    }
}
