package com.example.haftalikharajat.ui.history;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.model.CategoryStat;
import com.example.haftalikharajat.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class CategoryBreakdownAdapter extends RecyclerView.Adapter<CategoryBreakdownAdapter.CategoryViewHolder> {

    private final List<CategoryStat> items = new ArrayList<>();
    private final int[] colors = {
            R.color.chart_cat_1,
            R.color.chart_cat_2,
            R.color.chart_cat_3,
            R.color.chart_cat_4,
            R.color.chart_cat_5
    };

    public void setItems(List<CategoryStat> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_stat, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(items.get(position), colors[position % colors.length]);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final View vDot;
        private final TextView tvName;
        private final TextView tvPercent;
        private final TextView tvAmount;
        private final ProgressBar progressBar;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            vDot = itemView.findViewById(R.id.vCategoryDot);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            tvPercent = itemView.findViewById(R.id.tvCategoryPercent);
            tvAmount = itemView.findViewById(R.id.tvCategoryAmount);
            progressBar = itemView.findViewById(R.id.pbCategoryProgress);
        }

        public void bind(CategoryStat item, int colorRes) {
            Context context = itemView.getContext();
            int color = ContextCompat.getColor(context, colorRes);

            vDot.setBackgroundTintList(ColorStateList.valueOf(color));
            tvName.setText(item.getCategory());
            tvPercent.setText(item.getPercentage() + "%");
            tvAmount.setText(CurrencyUtils.formatAmount(item.getAmount()));

            progressBar.setProgress(item.getPercentage());
            progressBar.setProgressTintList(ColorStateList.valueOf(color));
        }
    }
}
