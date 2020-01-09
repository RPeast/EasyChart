package com.antony.library.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointD;


public class MyCombinedChart extends CombinedChart {
    private MyLeftMarkerView myMarkerViewLeft;
    private MyBottomMarkerView bottomMarkerView;

    private int drawOrders = -1;

    public MyCombinedChart(Context context) {
        super(context);
    }

    public MyCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDrawOrders(int i) {
        this.drawOrders = i;
    }


    public void setMarker(MyLeftMarkerView markerLeft, MyBottomMarkerView bottomMarkerView) {
        this.myMarkerViewLeft = markerLeft;
        this.bottomMarkerView = bottomMarkerView;
    }


    protected float getYPixelForValues(float x, float y) {
        MPPointD pixels = getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(x, y);
        return (float) pixels.y;
    }


    @Override
    protected void drawMarkers(Canvas canvas) {
        if (!mDrawMarkers || !valuesToHighlight())
            return;
        switch (drawOrders) {
            case ChartType.K_CHART:
                drawKlineMarkers(canvas);
                break;
            case ChartType.LINE_CHART:
                drawLineMarkers(canvas);
                break;
            case ChartType.BAR_CHART:
                drawBarMarkers(canvas);
                break;
        }

    }

    private void drawBarMarkers(Canvas canvas) {
        BarData barData = getBarData();
        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            IBarDataSet set =  barData.getDataSetByIndex(highlight.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled())
                continue;
            Entry e = set.getEntryForXValue(highlight.getX(), highlight.getY());

            float y = highlight.getDrawY();

            Entry ce = set.getEntryForXValue(highlight.getX(),highlight.getY());

            float lowValue = ce.getX() * mAnimator.getPhaseY();
            float highValue = ce.getY() * mAnimator.getPhaseY();
            MPPointD pix = getTransformer(set.getAxisDependency()).getPixelForValues(highlight.getX(), (lowValue + highValue) / 2f);
            float xp = (float) pix.x;
            setMarkerView(e, i, canvas, xp, y);
        }
    }

    private void drawKlineMarkers(Canvas canvas) {
        CandleData candleData = getCandleData();
        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            ICandleDataSet set = candleData.getDataSetByIndex(highlight.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;
            Entry e = set.getEntryForXValue(highlight.getX(), highlight.getY());

            float y = highlight.getDrawY();
            CandleEntry ce = set.getEntryForXValue(highlight.getX(), highlight.getY());
            float lowValue = ce.getLow() * mAnimator.getPhaseY();
            float highValue = ce.getHigh() * mAnimator.getPhaseY();
            MPPointD pix = getTransformer(set.getAxisDependency()).getPixelForValues(highlight.getX(), (lowValue + highValue) / 2f);
            float xp = (float) pix.x;

            setMarkerView(e, i, canvas, xp, y);


        }
    }

    private void drawLineMarkers(Canvas canvas){
        LineData lineData = getLineData();
        for (int i = 0; i < mIndicesToHighlight.length; i++) {
            Highlight highlight = mIndicesToHighlight[i];
            ILineDataSet set = lineData.getDataSetByIndex(highlight.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled())
                continue;
            Entry e = set.getEntryForXValue(highlight.getX(), highlight.getY());

            float y = highlight.getDrawY();

            Entry ce = set.getEntryForXValue(highlight.getX(),highlight.getY());

            float lowValue = ce.getX() * mAnimator.getPhaseY();
            float highValue = ce.getY() * mAnimator.getPhaseY();
            MPPointD pix = getTransformer(set.getAxisDependency()).getPixelForValues(highlight.getX(), (lowValue + highValue) / 2f);
            float xp = (float) pix.x;
            setMarkerView(e, i, canvas, xp, y);
        }
    }

    private void setMarkerView(Entry e, int i, Canvas canvas, float xp, float y) {

        if (null != bottomMarkerView) {
            //修改标记值

            bottomMarkerView.setData(e.getData().toString());

            bottomMarkerView.refreshContent(e, mIndicesToHighlight[i]);

            bottomMarkerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            bottomMarkerView.layout(0, 0, bottomMarkerView.getMeasuredWidth(),
                    bottomMarkerView.getMeasuredHeight());

            float posX = xp - bottomMarkerView.getWidth() / 2;

            if (xp + bottomMarkerView.getWidth() / 2 > getWidth()) {
                posX = getWidth() - bottomMarkerView.getWidth();
            }

            bottomMarkerView.draw(canvas, posX, mViewPortHandler.contentBottom());

        }
        if (y > 0) {

            float yMaxValue = getYChartMax();
            float yMinValue = getYChartMin();
            float yMin = getYPixelForValues(xp, yMaxValue);
            float yMax = getYPixelForValues(xp, yMinValue);
            float yValue = (yMax - y) / (yMax - yMin) * (yMaxValue - yMinValue) + yMinValue;
            if (y < mViewPortHandler.contentBottom()) {
                if (null != myMarkerViewLeft) {
                    //修改标记值

                    myMarkerViewLeft.setData(yValue);

                    myMarkerViewLeft.refreshContent(e, mIndicesToHighlight[i]);

                    myMarkerViewLeft.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    myMarkerViewLeft.layout(0, 0, myMarkerViewLeft.getMeasuredWidth(),
                            myMarkerViewLeft.getMeasuredHeight());
                    myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft(), mIndicesToHighlight[i].getDrawY() - myMarkerViewLeft.getHeight() / 2);


                }
            }


        }
    }
}
