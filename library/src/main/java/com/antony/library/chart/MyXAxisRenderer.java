package com.antony.library.chart;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class MyXAxisRenderer extends XAxisRenderer {
    protected int interval;

    public MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        this(viewPortHandler, xAxis, trans, 0);
    }

    public MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans,
                           int interval) {
        super(viewPortHandler, xAxis, trans);
        this.interval = interval;
    }

    @Override
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {
        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];
        for (int i = 0; i < positions.length; i += 2) {
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            }
        }
        mTrans.pointValuesToPixel(positions);

        for (int i = 0; i < positions.length; i += 2) {
            float x = positions[i];
            if (mViewPortHandler.isInBoundsX(x)) {
                String label = mXAxis.getValueFormatter().getFormattedValue(mXAxis.mEntries[i / 2], mXAxis);
                if(mXAxis.isAvoidFirstLastClippingEnabled()){
                    if (label==null)continue;
                    float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                    if (i == mXAxis.mEntryCount * 2 - 2 && mXAxis.mEntryCount > 1) {
                        x -= width / 2 + interval;
                    } else if (i == 0) {
                        x += width / 2 + interval;
                    }
                }
                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
            }
        }

    }
}
