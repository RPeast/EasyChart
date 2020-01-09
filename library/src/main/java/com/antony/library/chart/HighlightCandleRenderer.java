package com.antony.library.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.blankj.utilcode.util.ScreenUtils;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.List;


/**
 * 自定义CandleStickChart渲染器 绘制高亮  -- 绘制方式和自定义LineChart渲染器相同
 * 使用方法: 1.先设置渲染器 {@link CombinedChart#setRenderer(DataRenderer)}
 * 传入自定义渲染器 将其中Candle图的渲染器替换成此渲染器
 * 2.设置数据时 调用 {@link CandleEntry#CandleEntry(float, float, float, float, float, Object)}
 * 传入String类型的data 以绘制x的值  -- 如未设置 则只绘制竖线
 */
public class HighlightCandleRenderer extends CandleStickChartRenderer {

    private float highlightSize;//图表高亮文字大小 单位:px
    private DecimalFormat format = new DecimalFormat("0.0000");
    private Highlight[] indices;

    public HighlightCandleRenderer(CandleDataProvider chart, ChartAnimator animator,
                                   ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);

    }

    public HighlightCandleRenderer setHighlightSize(float textSize) {
        highlightSize = textSize;
        return this;
    }


    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        this.indices = indices;
    }

    protected float getYPixelForValues(float x, float y) {
        MPPointD pixels = mChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(x, y);
        return (float) pixels.y;
    }

    public void drawLowPoint(CandleDataSet set, Transformer trans, Paint paint, Canvas c) {
        float[] minFloat =  getMinFloat(set.getValues());
        //通过trans得到最低点的屏幕位置
        MPPointD minPoint = trans.getPixelForValues(minFloat[0],minFloat[1]);
        float lowX = (float)minPoint.x;
        float lowY = (float)minPoint.y;
        paint.setColor(Color.parseColor("#1ab546"));
        float rectLength = Utils.convertDpToPixel((minFloat[1] +"").length() * Utils.convertDpToPixel(1.7f));//矩形框长
        float vLength = Utils.convertDpToPixel(10f);//竖线长10dp
        float hLength = Utils.convertDpToPixel(15f);//横线长15dp
        float rect= Utils.convertDpToPixel(8f);//矩形高低差/2
        float textX= Utils.convertDpToPixel(2f);//文本x坐标偏移量
        float textY= Utils.convertDpToPixel(3f);//文本y偏移量
        c.drawLine(lowX,lowY,lowX,lowY +vLength,paint);
        int mWidth = ScreenUtils.getScreenWidth();
        if(lowX>mWidth-mWidth/3){
            c.drawLine(lowX,lowY +vLength,lowX -hLength,lowY +vLength,paint);
            int i = (int) (lowX -hLength- rectLength);
            int j = (int) (lowY +vLength-rect);
            int k = (int) (lowX -hLength);
            int f = (int) (lowY +vLength+rect);
            Rect mRect = new Rect(i,j,k,f);
            c.drawRect(mRect,paint);
            //写数字
            paint.setColor(Color.WHITE);
            c.drawText(minFloat[1] +"",lowX - rectLength -hLength+textX,lowY +vLength+textY,paint);
        }else{
            c.drawLine(lowX,lowY +vLength,lowX +hLength,lowY +vLength,paint);
            int i = (int) (lowX +hLength);
            int j = (int) (lowY +vLength-rect);
            int k = (int) (lowX +hLength+  rectLength);
            int f = (int) (lowY +vLength+rect);
            Rect mRect = new Rect(i,j,k,f);
            c.drawRect(mRect,paint);
            paint.setColor(Color.WHITE);
            c.drawText(minFloat[1] +"",lowX +hLength+textX,lowY +vLength+textY,paint);
        }
    }

    private float[] getMinFloat(List<CandleEntry> lists) {
        float[] mixEntry =new float[2];
        for (int i =0;i<lists.size()-1;i++){
            if(i ==0) {
                mixEntry[0] = lists.get(i).getX();

                mixEntry[1] = lists.get(i).getY();
            }
            if(mixEntry[1]>lists.get(i+1).getY()){
                mixEntry[0] = lists.get(i +1).getX();
                mixEntry[1] = lists.get(i +1).getY();
            }

        }
        return mixEntry;
    }



    @Override
    public void drawValues(Canvas c) {
//        super.drawValues(c);
//        CandleDataSet set = (CandleDataSet) mChart.getCandleData().getDataSetByIndex(0);
//        Transformer transformer = mChart.getTransformer(set.getAxisDependency());
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿画笔
//        paint.setTextSize(Utils.convertDpToPixel(10f));//设置字体大小
//
//
//        drawLowPoint(set, transformer, paint, c);

        List<ICandleDataSet> dataSets =  mChart.getCandleData().getDataSets();
        for (int i = 0; i < dataSets.size(); i++) {

            ICandleDataSet dataSet = dataSets.get(i);
//            if (!dataSet.isDrawValuesEnabled() || dataSet.getEntryCount() == 0)
//                continue;

            if(!shouldDrawValues(dataSet))
                continue;

            applyValueTextStyle(dataSet);

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            mXBounds.set(mChart, dataSet);

            int minx = Math.max(mXBounds.min, 0);

            int maxx = Math.min(mXBounds.max, dataSet.getEntryCount());

            float[] positions = trans.generateTransformedValuesCandle(
                    dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), minx, maxx);

            //计算最大值和最小值
            float maxValue = 0,minValue = 0;
            int maxIndex = 0 , minIndex = 0;
            CandleEntry maxEntry = null, minEntry = null;
            boolean firstInit = true;

            for (int j = 0; j < positions.length; j += 2) {

                float x = positions[j];
                float y = positions[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x))
                    break;

                if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y))
                    continue;

                CandleEntry entry = dataSet.getEntryForIndex(j / 2 + minx);

                if (firstInit){
                    maxValue = entry.getHigh();
                    minValue = entry.getLow();
                    firstInit = false;
                    maxEntry = entry;
                    minEntry = entry;
                }else{
                    if (entry.getHigh() > maxValue)
                    {
                        maxValue = entry.getHigh();
                        maxIndex = j;
                        maxEntry = entry;
                    }

                    if (entry.getLow() < minValue){
                        minValue = entry.getLow();
                        minIndex = j;
                        minEntry = entry;
                    }

                }
            }

            //绘制最大值和最小值
            float x = positions[minIndex];
            float y = positions[minIndex + 1];

            if (maxIndex > minIndex){
                //画右边
                String highString = Float.toString(minValue);

                //计算显示位置
                //计算文本宽度
                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString);
                int highStringHeight = Utils.calcTextHeight(mValuePaint, highString);

                float[] tPosition=new float[2];
                tPosition[1]=minValue;
                trans.pointValuesToPixel(tPosition);

                mValuePaint.setColor(dataSet.getValueTextColor(minIndex / 2));
                //画竖线
                c.drawLine(x,tPosition[1],x+highStringWidth/2,tPosition[1]+highStringHeight,mValuePaint);
                c.drawText(highString, x + highStringWidth,tPosition[1]+highStringHeight*2, mValuePaint);
            }else{
                //画左边
                String highString = Float.toString(minValue);

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString);
                int highStringHeight = Utils.calcTextHeight(mValuePaint, highString);

                    /*mValuePaint.setColor(dataSet.getValueTextColor(minIndex / 2));
                    c.drawText(highString, x-highStringWidth/2, y+yOffset, mValuePaint);*/
                float[] tPosition=new float[2];
                tPosition[1]=minValue;
                trans.pointValuesToPixel(tPosition);
                mValuePaint.setColor(dataSet.getValueTextColor(minIndex / 2));
                c.drawLine(x,tPosition[1],x-highStringWidth/2,tPosition[1]+highStringHeight,mValuePaint);
                c.drawText(highString, x - highStringWidth, tPosition[1]+highStringHeight*2, mValuePaint);
            }

            x = positions[maxIndex];
            y = positions[maxIndex + 1];

            if (maxIndex > minIndex){
                //画左边
                String highString = Float.toString(maxValue);

                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString);
                int highStringHeight = Utils.calcTextHeight(mValuePaint, highString);

                float[] tPosition=new float[2];
                tPosition[0] = maxEntry == null ? 0f:maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f:maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);

                mValuePaint.setColor(dataSet.getValueTextColor(maxIndex / 2));

                c.drawLine(x,tPosition[1],x-highStringWidth/2,tPosition[1]-highStringHeight,mValuePaint);

                c.drawText(highString, x - highStringWidth, tPosition[1]-highStringHeight/2, mValuePaint);

            }else{
                //画右边
                String highString =  Float.toString(maxValue);

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString);
                int highStringHeight = Utils.calcTextHeight(mValuePaint, highString);

                float[] tPosition=new float[2];
                tPosition[0] = maxEntry == null ? 0f:maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f:maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);

                mValuePaint.setColor(dataSet.getValueTextColor(maxIndex / 2));
                c.drawLine(x,tPosition[1],x+highStringWidth/2,tPosition[1]-highStringHeight,mValuePaint);

                c.drawText(highString, x + highStringWidth, tPosition[1]-highStringHeight, mValuePaint);

            }
        }


    }


    @Override
    public void drawExtras(Canvas c) {
        if (indices == null) {
            return;
        }
        CandleData candleData = mChart.getCandleData();
        for (Highlight high : indices) {
            ICandleDataSet set = candleData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled())
                continue;

            CandleEntry e = set.getEntryForXValue(high.getX(), high.getY());
            if (!isInBoundsX(e, set))
                continue;

            float lowValue = e.getLow() * mAnimator.getPhaseY();
            float highValue = e.getHigh() * mAnimator.getPhaseY();

            MPPointD pix = mChart.getTransformer(set.getAxisDependency())
                    .getPixelForValues(e.getX(), (lowValue + highValue) / 2f);
            float xp = (float) pix.x;

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());
            mHighlightPaint.setTextSize(highlightSize);

            float xMin = mViewPortHandler.contentLeft();
            float xMax = mViewPortHandler.contentRight();
            float contentBottom = mViewPortHandler.contentBottom();
            //画竖线
            int halfPaddingVer = 5;//竖向半个padding
            int halfPaddingHor = 8;
            float textXHeight = 0;

            String textX;//高亮点的X显示文字
            Object data = e.getData();
            if (data != null && data instanceof String) {
                textX = (String) data;
            } else {
                textX = e.getX() + "";
            }
            if (!TextUtils.isEmpty(textX)) {//绘制x的值
                //先绘制文字框
                mHighlightPaint.setStyle(Paint.Style.STROKE);
                int width = Utils.calcTextWidth(mHighlightPaint, textX);
                int height = Utils.calcTextHeight(mHighlightPaint, textX);
                float left = Math.max(xMin, xp - width / 2F - halfPaddingVer);//考虑间隙
                float right = left + width + halfPaddingHor * 2;
                if (right > xMax) {
                    right = xMax;
                    left = right - width - halfPaddingHor * 2;
                }
                textXHeight = height + halfPaddingVer * 2;
                RectF rect = new RectF(left, 0, right, textXHeight);
//                c.drawRect(rect, mHighlightPaint);
                //再绘制文字
                mHighlightPaint.setStyle(Paint.Style.FILL);
                Paint.FontMetrics metrics = mHighlightPaint.getFontMetrics();
                float baseY = (height + halfPaddingVer * 2 - metrics.top - metrics.bottom) / 2;
//                c.drawText(textX, left + halfPaddingHor, baseY, mHighlightPaint);
            }
            //绘制竖线
            c.drawLine(xp, textXHeight, xp, mChart.getHeight(), mHighlightPaint);

            //判断是否画横线
            float y = high.getDrawY();
            float yMaxValue = mChart.getYChartMax();
            float yMinValue = mChart.getYChartMin();
            float yMin = getYPixelForValues(xp, yMaxValue);
            float yMax = getYPixelForValues(xp, yMinValue);
            if (y > 0 && y <= contentBottom) {//在区域内即绘制横线
                //先绘制文字框
                mHighlightPaint.setStyle(Paint.Style.STROKE);
                float yValue = (yMax - y) / (yMax - yMin) * (yMaxValue - yMinValue) + yMinValue;
                String text = format.format(yValue);
                int width = Utils.calcTextWidth(mHighlightPaint, text);
                int height = Utils.calcTextHeight(mHighlightPaint, text);
                float top = Math.max(0, y - height / 2F - halfPaddingVer);//考虑间隙
                float bottom = top + height + halfPaddingVer * 2;
                if (bottom > contentBottom) {
                    bottom = contentBottom;
                    top = bottom - height - halfPaddingVer * 2;
                }
                RectF rect = new RectF(xMax - width - halfPaddingHor * 2, top, xMax, bottom);

//                c.drawRect(rect, mHighlightPaint);
                //再绘制文字
                mHighlightPaint.setStyle(Paint.Style.FILL);
                Paint.FontMetrics metrics = mHighlightPaint.getFontMetrics();
                float baseY = (top + bottom - metrics.top - metrics.bottom) / 2;
//                c.drawText(text, xMax - width - halfPaddingHor, baseY, mHighlightPaint);
                //绘制横线
                c.drawLine(0, y, xMax, y, mHighlightPaint);
            }
        }
        indices = null;
    }
}
