package com.po.sample.hanoi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class PillarLayout extends LinearLayout {
    public PillarLayout(Context context) {
        this(context, null);
    }

    public PillarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PillarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
    }

    void loadDisk(int totalDisk) {
        float weight = 0.99f;


        for (int i = 0; i < totalDisk; i++) {
            DiskView diskView = new DiskView(getContext());
            diskView.setDiskId(i);
            diskView.setDiskWeight((i + 1) * weight / totalDisk);

            addView(diskView, i);
        }
    }

    void addDisk(DiskView diskView) {
        addView(diskView, 0);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);

        Animation drop = AnimationUtils.loadAnimation(getContext(), R.anim.disk_drop);
        child.startAnimation(drop);
    }

    DiskView removeDisk(int diskId) {
        for (int i = 0;  i < getChildCount(); i++) {
            DiskView diskView = (DiskView)getChildAt(i);
            if (diskId == diskView.getDiskId()) {
                removeView(diskView);
                return diskView;
            }
        }

        return null;
    }
}
