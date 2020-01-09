package com.antony.library.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointD;

public class MyCombinedBarChart extends CombinedChart {
    private MyLeftMarkerView myMarkerViewLeft;
    public MyCombinedBarChart(Context context) {
        super(context);
    }

    public MyCombinedBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCombinedBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMarker(MyLeftMarkerView markerLeft) {
        this.myMarkerViewLeft = markerLeft;
    }

    protected float getYPixelForValues(float x, float y) {
        MPPointD pixels = getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(x, y);
        return (float) pixels.y;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void drawMarkers(Canvas canvas) {
        if (!mDrawMarkers || !valuesToHighlight())
            return;
        BarData barData = getBarData();
        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            IBarDataSet set = barData.getDataSetByIndex(highlight.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled())
                continue;
            Entry e = set.getEntryForXValue(highlight.getX(), highlight.getY());

            float y = highlight.getDrawY();





            if (y > 0 ) {

                BarEntry be = set.getEntryForXValue(highlight.getX(), highlight.getY());
                float lowValue = be.getNegativeSum() * mAnimator.getPhaseY();
                float highValue = be.getPositiveSum() * mAnimator.getPhaseY();
                MPPointD pix = getTransformer(set.getAxisDependency()).getPixelForValues(highlight.getX(), (lowValue + highValue) / 2f);
                float xp = (float) pix.x;
                float yMaxValue = getYChartMax();
                float yMinValue = getYChartMin();
                float yMin = getYPixelForValues(xp, yMaxValue);
                float yMax = getYPixelForValues(xp, yMinValue);
                float yValue = (yMax - y) / (yMax - yMin) * (yMaxValue - yMinValue) + yMinValue;

                if (null != myMarkerViewLeft) {
                    //修改标记值

                    myMarkerViewLeft.setData(yValue);

                    myMarkerViewLeft.refreshContent(e, mIndicesToHighlight[i]);

                    myMarkerViewLeft.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    myMarkerViewLeft.layout(0, 0, myMarkerViewLeft.getMeasuredWidth(),
                            myMarkerViewLeft.getMeasuredHeight());

                    myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft(), highlight.getDrawY() - myMarkerViewLeft.getHeight() / 2);

                    myMarkerViewLeft.draw(canvas, xp - myMarkerViewLeft.getWidth() / 2, mViewPortHandler.contentBottom());

                }
            }


        }
    }
}
