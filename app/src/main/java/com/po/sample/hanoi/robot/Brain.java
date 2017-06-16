package com.po.sample.hanoi.robot;


import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;

public final class Brain {
    interface Callback {
        void onMoveDisk(int disk, int from, int to);
        void onFinished();
        void onCanceled();
    }

    private final Callback callback;
    private final CancellationSignal cancel;

    public static void think(int totalDisk, Callback callback, CancellationSignal cancel) {
        try {
            Brain brain = new Brain(callback, cancel);
            brain.solveHanoi(totalDisk, totalDisk - 1, 0, 2, 1);

            callback.onFinished();
        } catch (OperationCanceledException e) {
            callback.onCanceled();
        }
    }

    private Brain(Callback callback, CancellationSignal cancel) {
        this.callback = callback;
        this.cancel = cancel;
    }

    private void solveHanoi(int totalDisk, int maxDiskId, int from, int to, int spare)
            throws OperationCanceledException {
        this.cancel.throwIfCanceled();

        if (totalDisk == 1) {
            this.callback.onMoveDisk(maxDiskId, from, to);
        } else {
            solveHanoi(totalDisk - 1, maxDiskId - 1, from, spare, to);
            this.callback.onMoveDisk(maxDiskId, from, to);
            solveHanoi(totalDisk - 1, maxDiskId - 1, spare, to, from);
        }
    }
}
