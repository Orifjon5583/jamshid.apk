package com.example.haftalikharajat.ui.history;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.haftalikharajat.R;
import com.example.haftalikharajat.data.model.DayStat;

import java.util.ArrayList;
import java.util.List;

public class WeeklyBarChartView extends View {

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint barBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<DayStat> dayStats = new ArrayList<>();
    private final RectF rect = new RectF();
    private final RectF badgeRect = new RectF();

    private int activeBarColor;
    private int defaultBarColor;
    private int barBgColor;
    private int textColor;
    private int highlightTextColor;
    private int selectedIndex = 4; // Default to Juma (Friday) or current day

    private OnDaySelectedListener onDaySelectedListener;

    public interface OnDaySelectedListener {
        void onDaySelected(DayStat dayStat, int index);
    }

    public WeeklyBarChartView(Context context) {
        super(context);
        init();
    }

    public WeeklyBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeeklyBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        activeBarColor = ContextCompat.getColor(getContext(), R.color.primary);
        defaultBarColor = ContextCompat.getColor(getContext(), R.color.chart_bar_default);
        barBgColor = ContextCompat.getColor(getContext(), R.color.surface_card_secondary);
        textColor = ContextCompat.getColor(getContext(), R.color.text_secondary);
        highlightTextColor = ContextCompat.getColor(getContext(), R.color.text_on_primary);

        textPaint.setTextSize(spToPx(11));
        textPaint.setTextAlign(Paint.Align.CENTER);

        highlightTextPaint.setTextSize(spToPx(11));
        highlightTextPaint.setTextAlign(Paint.Align.CENTER);
        highlightTextPaint.setColor(highlightTextColor);
        highlightTextPaint.setFakeBoldText(true);

        badgePaint.setColor(activeBarColor);
    }

    public void setDayStats(List<DayStat> stats) {
        dayStats.clear();
        if (stats != null) {
            dayStats.addAll(stats);
            for (int i = 0; i < dayStats.size(); i++) {
                if (dayStats.get(i).isHighlighted()) {
                    selectedIndex = i;
                    break;
                }
            }
        }
        invalidate();
    }

    public void setOnDaySelectedListener(OnDaySelectedListener listener) {
        this.onDaySelectedListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dayStats.isEmpty()) return;

        int count = dayStats.size();
        float width = getWidth();
        float height = getHeight();

        float bottomPadding = dpToPx(32);
        float topPadding = dpToPx(8);
        float chartHeight = height - bottomPadding - topPadding;

        // Find max amount
        double maxAmount = 1;
        for (DayStat stat : dayStats) {
            if (stat.getAmount() > maxAmount) {
                maxAmount = stat.getAmount();
            }
        }

        float colWidth = width / count;
        float barWidth = Math.min(dpToPx(18), colWidth * 0.45f);
        float cornerRadius = dpToPx(6);

        for (int i = 0; i < count; i++) {
            DayStat stat = dayStats.get(i);
            float cx = i * colWidth + (colWidth / 2f);

            // Draw background track
            float left = cx - (barWidth / 2f);
            float right = cx + (barWidth / 2f);
            float top = topPadding;
            float bottom = height - bottomPadding;

            rect.set(left, top, right, bottom);
            barBgPaint.setColor(barBgColor);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, barBgPaint);

            // Draw filled bar
            float fillHeight = (float) ((stat.getAmount() / maxAmount) * chartHeight);
            if (fillHeight < dpToPx(6) && stat.getAmount() > 0) {
                fillHeight = dpToPx(6);
            }
            float fillTop = bottom - fillHeight;

            rect.set(left, fillTop, right, bottom);
            barPaint.setColor(i == selectedIndex ? activeBarColor : defaultBarColor);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, barPaint);

            // Draw day label underneath
            float labelY = height - dpToPx(10);
            if (i == selectedIndex) {
                // Highlighted badge
                float badgeW = dpToPx(34);
                float badgeH = dpToPx(20);
                badgeRect.set(cx - badgeW / 2f, height - bottomPadding + dpToPx(4),
                        cx + badgeW / 2f, height - bottomPadding + dpToPx(4) + badgeH);
                canvas.drawRoundRect(badgeRect, dpToPx(6), dpToPx(6), badgePaint);
                canvas.drawText(stat.getDayName(), cx, height - bottomPadding + dpToPx(18), highlightTextPaint);
            } else {
                textPaint.setColor(textColor);
                canvas.drawText(stat.getDayName(), cx, labelY, textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            int count = dayStats.size();
            if (count > 0) {
                float colWidth = getWidth() / (float) count;
                int index = (int) (x / colWidth);
                if (index >= 0 && index < count) {
                    selectedIndex = index;
                    invalidate();
                    if (onDaySelectedListener != null) {
                        onDaySelectedListener.onDaySelected(dayStats.get(index), index);
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }
}
