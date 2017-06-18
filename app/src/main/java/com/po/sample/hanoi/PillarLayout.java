package com.po.sample.hanoi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

public class PillarLayout extends LinearLayout {
    interface Callback {
        void onDiskRemoved(final DiskView disk);
    }

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

        Animation drop = AnimationUtils.loadAnimation(getContext(), R.anim.disk_drop);
        LayoutAnimationController animationController = new LayoutAnimationController(drop);
        animationController.setOrder(LayoutAnimationController.ORDER_REVERSE);
        animationController.setDelay(0.2f);

        setLayoutAnimation(animationController);
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

    void addDisk(final DiskView diskView) {
        addView(diskView, 0);

        Animation drop = AnimationUtils.loadAnimation(getContext(), R.anim.disk_drop);
        diskView.startAnimation(drop);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
    }

    void removeDisk(int diskId, boolean toRight, final Callback callback) {
        for (int i = 0;  i < getChildCount(); i++) {
            final DiskView diskView = (DiskView)getChildAt(i);
            if (diskId == diskView.getDiskId()) {
                    Animation remove = toRight ?
                                        AnimationUtils.loadAnimation(getContext(), R.anim.disk_move_right) :
                                        AnimationUtils.loadAnimation(getContext(), R.anim.disk_move_left);
                    remove.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            diskView.post(new Runnable() {
                                @Override
                                public void run() {
                                    removeView(diskView);
                                    clearDisappearingChildren();
                                    callback.onDiskRemoved(diskView);
                                }
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    diskView.startAnimation(remove);

                    return;
                }
            }
        }
}
