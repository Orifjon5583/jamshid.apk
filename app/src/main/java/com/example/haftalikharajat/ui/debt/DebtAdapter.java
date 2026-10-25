package com.example.haftalikharajat.ui.debt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.model.Debt;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.example.haftalikharajat.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {

    private final List<Debt> items = new ArrayList<>();
    private final OnDebtActionListener listener;

    public interface OnDebtActionListener {
        void onEdit(Debt debt);
        void onDelete(Debt debt);
        void onMarkPaid(Debt debt);
    }

    public DebtAdapter(OnDebtActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Debt> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DebtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_debt, parent, false);
        return new DebtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DebtViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DebtViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPersonName;
        private final TextView tvDebtTypeDesc;
        private final TextView tvStatusBadge;
        private final TextView tvDueDate;
        private final TextView tvPhone;
        private final TextView tvAmountLabel;
        private final TextView tvAmount;
        private final MaterialButton btnCall;
        private final MaterialButton btnMarkPaid;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPersonName = itemView.findViewById(R.id.tvPersonName);
            tvDebtTypeDesc = itemView.findViewById(R.id.tvDebtTypeDesc);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAmountLabel = itemView.findViewById(R.id.tvAmountLabel);
            tvAmount = itemView.findViewById(R.id.tvDebtAmount);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnMarkPaid = itemView.findViewById(R.id.btnMarkPaid);
            btnEdit = itemView.findViewById(R.id.btnEditDebt);
            btnDelete = itemView.findViewById(R.id.btnDeleteDebt);
        }

        public void bind(Debt item, OnDebtActionListener listener) {
            Context context = itemView.getContext();
            tvPersonName.setText(item.getPersonName());

            boolean isLent = item.isLent();
            tvDebtTypeDesc.setText(isLent ? "Menga berishadi" : "Men berishim kerak");
            tvAmountLabel.setText(isLent ? "Kutilayotgan summa:" : "Majburiyat summasi:");
            tvAmount.setText(CurrencyUtils.formatAmount(item.getAmount()));

            // Status badge logic
            if (item.isClosed()) {
                tvStatusBadge.setText("To'langan");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_rounded);
                tvStatusBadge.getBackground().setTint(ContextCompat.getColor(context, R.color.income_bg));
                tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.income_text));
                btnMarkPaid.setVisibility(View.GONE);
                tvDueDate.setText("Yopilgan sana: " + DateUtils.formatUzbekDate(item.getClosedDate()));
            } else {
                int daysDiff = DateUtils.getDaysDiffFromToday(item.getDueDate());
                if (daysDiff < 0) {
                    // Overdue
                    int absDays = Math.abs(daysDiff);
                    tvStatusBadge.setText("Muddati o'tgan (" + absDays + " kun)");
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_rounded);
                    tvStatusBadge.getBackground().setTint(ContextCompat.getColor(context, R.color.expense_bg));
                    tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.expense_text));
                } else if (daysDiff == 0) {
                    // Due today
                    tvStatusBadge.setText("Bugun to'lash kerak");
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_rounded);
                    tvStatusBadge.getBackground().setTint(ContextCompat.getColor(context, R.color.warning_bg));
                    tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.warning_text));
                } else {
                    // On track
                    tvStatusBadge.setText("Rejali");
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_rounded);
                    tvStatusBadge.getBackground().setTint(ContextCompat.getColor(context, R.color.primary_soft));
                    tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary));
                }
                tvDueDate.setText("Muddat: " + DateUtils.formatUzbekDate(item.getDueDate()));
                btnMarkPaid.setVisibility(View.VISIBLE);
            }

            // Phone & Call button
            if (item.getPhone() != null && !item.getPhone().trim().isEmpty()) {
                tvPhone.setVisibility(View.VISIBLE);
                tvPhone.setText(item.getPhone());
                btnCall.setVisibility(View.VISIBLE);
                btnCall.setOnClickListener(v -> {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.getPhone().trim()));
                    context.startActivity(dialIntent);
                });
            } else {
                tvPhone.setVisibility(View.GONE);
                btnCall.setVisibility(View.GONE);
            }

            btnMarkPaid.setOnClickListener(v -> {
                if (listener != null) listener.onMarkPaid(item);
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(item);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });
        }
    }
}
