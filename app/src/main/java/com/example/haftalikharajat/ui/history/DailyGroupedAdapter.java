package com.example.haftalikharajat.ui.history;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DailyGroupedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TRANSACTION = 1;

    public static class RowItem {
        final int type;
        final String headerTitle;
        final double headerTotal;
        final Transaction transaction;

        public RowItem(String headerTitle, double headerTotal) {
            this.type = TYPE_HEADER;
            this.headerTitle = headerTitle;
            this.headerTotal = headerTotal;
            this.transaction = null;
        }

        public RowItem(Transaction transaction) {
            this.type = TYPE_TRANSACTION;
            this.headerTitle = null;
            this.headerTotal = 0;
            this.transaction = transaction;
        }
    }

    private final List<RowItem> rows = new ArrayList<>();

    public void setTransactions(List<Transaction> transactions) {
        rows.clear();
        if (transactions != null && !transactions.isEmpty()) {
            // Group by date
            Map<String, List<Transaction>> map = new LinkedHashMap<>();
            for (Transaction t : transactions) {
                String d = t.getDate();
                if (!map.containsKey(d)) {
                    map.put(d, new ArrayList<>());
                }
                map.get(d).add(t);
            }

            for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {
                String date = entry.getKey();
                List<Transaction> list = entry.getValue();

                double sum = 0;
                for (Transaction t : list) {
                    if (t.isExpense()) {
                        sum += t.getAmount();
                    }
                }

                String rel = DateUtils.formatRelativeDate(date).toUpperCase();
                String header = DateUtils.formatUzbekDate(date).toUpperCase();
                if (rel.equals("BUGUN") || rel.equals("KECHA")) {
                    header = header + ", " + rel;
                }

                rows.add(new RowItem(header, sum));
                for (Transaction t : list) {
                    rows.add(new RowItem(t));
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
            return new TransactionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowItem item = rows.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item.headerTitle, item.headerTotal);
        } else if (holder instanceof TransactionViewHolder) {
            ((TransactionViewHolder) holder).bind(item.transaction);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvTotal;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDailyHeaderTitle);
            tvTotal = itemView.findViewById(R.id.tvDailyHeaderTotal);
        }

        public void bind(String title, double total) {
            tvTitle.setText(title);
            tvTotal.setText(CurrencyUtils.formatWithSign(total, true));
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategory;
        private final TextView tvTitle;
        private final TextView tvSubtitle;
        private final TextView tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategoryIcon);
            tvTitle = itemView.findViewById(R.id.tvExpenseTitle);
            tvSubtitle = itemView.findViewById(R.id.tvExpenseSubtitle);
            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
        }

        public void bind(Transaction item) {
            tvTitle.setText(item.getTitle());
            String time = (item.getTime() != null && !item.getTime().isEmpty()) ? item.getTime() : "";
            String cat = (item.getCategory() != null) ? " · " + item.getCategory() : "";
            tvSubtitle.setText(time + cat);

            if (item.isExpense()) {
                tvAmount.setText(CurrencyUtils.formatWithSign(item.getAmount(), true));
            } else {
                tvAmount.setText(CurrencyUtils.formatWithSign(item.getAmount(), false));
            }

            int iconRes = R.drawable.ic_category_other;
            String c = item.getCategory() != null ? item.getCategory().toLowerCase() : "";
            if (c.contains("oziq") || c.contains("ovqat") || c.contains("bozor")) {
                iconRes = R.drawable.ic_category_food;
            } else if (c.contains("transport") || c.contains("taksi")) {
                iconRes = R.drawable.ic_category_transport;
            } else if (c.contains("uy") || c.contains("ijara") || c.contains("kommunal")) {
                iconRes = R.drawable.ic_category_housing;
            } else if (c.contains("qahva") || c.contains("kafe") || c.contains("tushlik")) {
                iconRes = R.drawable.ic_category_cafe;
            } else if (c.contains("kiyim")) {
                iconRes = R.drawable.ic_category_clothing;
            }
            ivCategory.setImageResource(iconRes);
        }
    }
}
