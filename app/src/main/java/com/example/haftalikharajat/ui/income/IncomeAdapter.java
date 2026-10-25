package com.example.haftalikharajat.ui.income;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.model.Transaction;
import com.example.haftalikharajat.utils.CurrencyUtils;
import com.example.haftalikharajat.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private final List<Transaction> items = new ArrayList<>();
    private final OnIncomeActionListener actionListener;

    public interface OnIncomeActionListener {
        void onEdit(Transaction transaction);
        void onDelete(Transaction transaction);
    }

    public IncomeAdapter(OnIncomeActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setItems(List<Transaction> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        holder.bind(items.get(position), actionListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIncomeIcon;
        private final TextView tvTitle;
        private final TextView tvSubtitle;
        private final TextView tvAmount;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIncomeIcon = itemView.findViewById(R.id.ivIncomeIcon);
            tvTitle = itemView.findViewById(R.id.tvIncomeTitle);
            tvSubtitle = itemView.findViewById(R.id.tvIncomeSubtitle);
            tvAmount = itemView.findViewById(R.id.tvIncomeAmount);
            btnEdit = itemView.findViewById(R.id.btnEditIncome);
            btnDelete = itemView.findViewById(R.id.btnDeleteIncome);
        }

        public void bind(Transaction item, OnIncomeActionListener listener) {
            tvTitle.setText(item.getTitle());

            String account = (item.getAccountType() != null && !item.getAccountType().isEmpty())
                    ? item.getAccountType() : "Asosiy";
            String dateFormatted = DateUtils.formatUzbekDate(item.getDate());
            tvSubtitle.setText(account + " · " + dateFormatted);

            tvAmount.setText(CurrencyUtils.formatWithSign(item.getAmount(), false));

            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(item);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });
        }
    }
}
