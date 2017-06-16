package com.po.sample.hanoi.robot;

import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;

public final class Brain {
    public interface Callback {
        void onMoveDisk(int disk, int from, int to);
        void onFinished();
        void onCanceled();
    }

    private final Callback callback;
    private final CancellationSignal cancel;

    public static void solveHanoi(int totalDisk, Callback callback, CancellationSignal cancel) {
        try {
            Brain brain = new Brain(callback, cancel);
            brain.moveHanoiTower(totalDisk, totalDisk - 1, 0, 2, 1);

            callback.onFinished();
        } catch (OperationCanceledException e) {
            callback.onCanceled();
        }
    }

    private Brain(Callback callback, CancellationSignal cancel) {
        this.callback = callback;
        this.cancel = cancel;
    }

    private void moveHanoiTower(int totalDisk, int maxDiskId, int from, int to, int spare)
            throws OperationCanceledException {
        cancel.throwIfCanceled();

        if (totalDisk == 1) {
            callback.onMoveDisk(maxDiskId, from, to);
        } else {
            moveHanoiTower(totalDisk - 1, maxDiskId - 1, from, spare, to);
            callback.onMoveDisk(maxDiskId, from, to);
            moveHanoiTower(totalDisk - 1, maxDiskId - 1, spare, to, from);
        }
    }
}
