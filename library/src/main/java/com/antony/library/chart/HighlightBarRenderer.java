package com.antony.library.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class HighlightBarRenderer extends BarChartRenderer {

    protected float barOffset = -0.5F;//BarChart绘制时偏移多少个单位 --小于0时向左偏移

    private float highlightSize;//图表高亮文字大小 单位:px
    private DecimalFormat format = new DecimalFormat("0.0000");
    private Highlight[] indices;
    private RectF mBarShadowRectBuffer = new RectF();


    public HighlightBarRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    public HighlightBarRenderer setHighlightSize(float highlightSize) {
        this.highlightSize = highlightSize;
        return this;
    }

//    @Override
//    public void initBuffers() {
//        BarData barData = mChart.getBarData();
//        mBarBuffers = new OffsetBarBuffer[barData.getDataSetCount()];
//
//        for (int i = 0; i < mBarBuffers.length; i++) {
//            IBarDataSet set = barData.getDataSetByIndex(i);
//            mBarBuffers[i] = new OffsetBarBuffer(set.getEntryCount() * 4 *
//                    (set.isStacked() ? set.getStackSize() : 1), barData.getDataSetCount(),
//                    set.isStacked(), barOffset);
//        }
//    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));
        final boolean drawBorder = dataSet.getBarBorderWidth() > 0.f;
        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();
        if (mChart.isDrawBarShadowEnabled()) {
            mShadowPaint.setColor(dataSet.getBarShadowColor());

            BarData barData = mChart.getBarData();
            final float barWidth = barData.getBarWidth();
            final float barWidthHalf = barWidth / 2.0f;
            float x;

            for (int i = 0, count = Math.min((int) (Math.ceil((float) (dataSet.getEntryCount()) * phaseX)),
                    dataSet.getEntryCount()); i < count; i++) {

                BarEntry e = dataSet.getEntryForIndex(i);
                x = e.getX();
                mBarShadowRectBuffer.left = x - barWidthHalf;
                mBarShadowRectBuffer.right = x + barWidthHalf;
                trans.rectValueToPixel(mBarShadowRectBuffer);

                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right))
                    continue;
                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left))
                    break;

                mBarShadowRectBuffer.top = mViewPortHandler.contentTop();
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom();
                c.drawRect(mBarShadowRectBuffer, mShadowPaint);
            }
        }
        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);
        trans.pointValuesToPixel(buffer.buffer);

        int size = dataSet.getColors().size();
        final boolean isSingleColor = size == 1;
        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor());
        }

        for (int j = 0; j < buffer.size(); j += 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;
            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                BarEntry entry = dataSet.getEntryForIndex(j / 4);
                Object data = entry.getData();
                if (data == null || !(data instanceof Integer)) {
                    mRenderPaint.setColor(dataSet.getColor(j / 4));
                } else {
                    int i = (int) data;
                    mRenderPaint.setColor(size > 1 ? dataSet.getColors().get(i % size) : Color.BLACK);
                }
            }

            c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);
            if (drawBorder) {
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mBarBorderPaint);
            }
        }

    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        this.indices = indices;
    }

    protected float getYPixelForValues(float x, float y) {
        MPPointD pixels = mChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(x, y);
        return (float) pixels.y;
    }



    @Override
    public void drawExtras(Canvas c) {
        if (indices == null) {
            return;
        }
        BarData barData = mChart.getBarData();
        for (Highlight high : indices) {

            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled()) continue;
            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());
            if (!isInBoundsX(e, set)) continue;

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setStrokeWidth(Utils.convertDpToPixel(0.5F));
            mHighlightPaint.setTextSize(highlightSize);
            float contentBottom = mViewPortHandler.contentBottom();
            //画竖线
            float barWidth = barData.getBarWidth();
            Transformer trans = mChart.getTransformer(set.getAxisDependency());
            prepareBarHighlight(e.getX(), 0, 0, barWidth / 2, trans);

            float xp = mBarRect.centerX();
            float yp = high.getDrawY();

            c.drawLine(xp, mViewPortHandler.getContentRect().bottom, xp, 0, mHighlightPaint);

            float y = high.getDrawY();
            float yMaxValue = mChart.getYChartMax();
            float yMinValue = mChart.getYChartMin();
            float yMin = getYPixelForValues(xp, yMaxValue);
            float yMax = getYPixelForValues(xp, yMinValue);
            //画横线
            if (yp > 0 && yp <= contentBottom) {
                float yValue = (yMax - y) / (yMax - yMin) * (yMaxValue - yMinValue) + yMinValue;
                c.drawLine(0, yp, mViewPortHandler.getContentRect().right, yp, mHighlightPaint);
            }

        }
    }
}
