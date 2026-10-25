package com.example.haftalikharajat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.haftalikharajat.ui.debt.DebtFragment;
import com.example.haftalikharajat.ui.expense.ExpenseFragment;
import com.example.haftalikharajat.ui.history.HistoryFragment;
import com.example.haftalikharajat.ui.income.IncomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_POST_NOTIFICATIONS = 1001;

    private BottomNavigationView bottomNav;
    private final Fragment expenseFragment = new ExpenseFragment();
    private final Fragment incomeFragment = new IncomeFragment();
    private final Fragment historyFragment = new HistoryFragment();
    private final Fragment debtFragment = new DebtFragment();
    private Fragment activeFragment = expenseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);

        // Add fragments
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, debtFragment, "debt").hide(debtFragment)
                .add(R.id.fragmentContainer, historyFragment, "history").hide(historyFragment)
                .add(R.id.fragmentContainer, incomeFragment, "income").hide(incomeFragment)
                .add(R.id.fragmentContainer, expenseFragment, "expense")
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_expense) {
                switchFragment(expenseFragment);
                return true;
            } else if (id == R.id.nav_income) {
                switchFragment(incomeFragment);
                return true;
            } else if (id == R.id.nav_history) {
                switchFragment(historyFragment);
                return true;
            } else if (id == R.id.nav_debt) {
                switchFragment(debtFragment);
                return true;
            }
            return false;
        });

        // Check if intent launched with specific tab
        if (getIntent() != null && getIntent().hasExtra("target_tab")) {
            int targetTab = getIntent().getIntExtra("target_tab", R.id.nav_expense);
            navigateToTab(targetTab);
        }

        // Request notification permission on Android 13+
        requestNotificationPermissionIfNeeded();
    }

    private void switchFragment(Fragment target) {
        if (activeFragment != target) {
            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(target)
                    .commit();
            activeFragment = target;
        }
    }

    public void navigateToTab(int menuId) {
        bottomNav.setSelectedItemId(menuId);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_POST_NOTIFICATIONS
                );
            }
        }
    }
}
