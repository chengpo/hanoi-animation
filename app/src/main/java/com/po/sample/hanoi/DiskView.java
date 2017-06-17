package com.po.sample.hanoi;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

public class DiskView extends TextView {
    private int diskId = 1;
    private float diskWeight = 1.0f;

    public DiskView(Context context) {
        this(context, null);
    }

    public DiskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setPadding(0, 2, 0, 2);
        setGravity(Gravity.CENTER);
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        setBackgroundResource(R.drawable.disk_background);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    void setDiskId(int diskId) {
        this.diskId = diskId;
        setText(String.valueOf(diskId + 1));
    }

    int getDiskId() {
        return diskId;
    }

    void setDiskWeight(float diskWeight) {
        this.diskWeight = diskWeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int)(MeasureSpec.getSize(widthMeasureSpec) * diskWeight),
                                                        MeasureSpec.getMode(widthMeasureSpec));

         super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
