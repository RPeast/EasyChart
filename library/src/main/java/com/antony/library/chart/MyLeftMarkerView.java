package com.antony.library.chart;

import android.content.Context;
import android.widget.TextView;

import com.antony.library.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.DecimalFormat;


public class MyLeftMarkerView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private TextView markerTv;
    private float num;
    private  DecimalFormat mFormat;

    public MyLeftMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        mFormat=new DecimalFormat("#0.00");
        markerTv = (TextView) findViewById(R.id.marker_tv);
        markerTv.setTextSize(10);
    }

    public void setData(float num){
        this.num=num;
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        markerTv.setText(mFormat.format(num));
        markerTv.setTextColor(getResources().getColor(R.color.white));
    }


}
