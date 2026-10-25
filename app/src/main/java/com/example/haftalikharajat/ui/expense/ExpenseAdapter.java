package com.example.haftalikharajat.ui.expense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<Transaction> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public ExpenseAdapter(OnItemClickListener listener) {
        this.listener = listener;
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
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategory;
        private final TextView tvTitle;
        private final TextView tvSubtitle;
        private final TextView tvAmount;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategoryIcon);
            tvTitle = itemView.findViewById(R.id.tvExpenseTitle);
            tvSubtitle = itemView.findViewById(R.id.tvExpenseSubtitle);
            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
        }

        public void bind(Transaction item, OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());

            String relDate = DateUtils.formatRelativeDate(item.getDate());
            String time = (item.getTime() != null && !item.getTime().isEmpty()) ? ", " + item.getTime() : "";
            String category = (item.getCategory() != null) ? " · " + item.getCategory() : "";
            tvSubtitle.setText(relDate + time + category);

            tvAmount.setText(CurrencyUtils.formatWithSign(item.getAmount(), true));

            // Set icon by category
            int iconRes = R.drawable.ic_category_other;
            String cat = item.getCategory() != null ? item.getCategory().toLowerCase() : "";
            if (cat.contains("oziq") || cat.contains("ovqat") || cat.contains("bozor")) {
                iconRes = R.drawable.ic_category_food;
            } else if (cat.contains("transport") || cat.contains("taksi")) {
                iconRes = R.drawable.ic_category_transport;
            } else if (cat.contains("uy") || cat.contains("ijara") || cat.contains("kommunal")) {
                iconRes = R.drawable.ic_category_housing;
            } else if (cat.contains("qahva") || cat.contains("kafe") || cat.contains("tushlik")) {
                iconRes = R.drawable.ic_category_cafe;
            } else if (cat.contains("kiyim")) {
                iconRes = R.drawable.ic_category_clothing;
            }
            ivCategory.setImageResource(iconRes);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
