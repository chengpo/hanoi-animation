package com.po.sample.hanoi;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.po.sample.hanoi.robot.Brain;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Handler mainUIHandler;
    private HanoiAsyncTask hanoiTask;

    private class HanoiAsyncTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            final int totalDisk = params[0];
            final CancellationSignal cancellationSignal = new CancellationSignal();

            Brain.solveHanoi(totalDisk, new Brain.Callback() {
                @Override
                public void onMoveDisk(int disk, int from, int to) {
                    if (isCancelled()) {
                        cancellationSignal.cancel();
                    }

                    Message message = mainUIHandler.obtainMessage(disk, from, to);
                    mainUIHandler.sendMessage(message);
                }

                @Override
                public void onFinished() {
                }

                @Override
                public void onCanceled() {

                }
            }, cancellationSignal);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            hanoiTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            hanoiTask = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainUIHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int disk = msg.what;
                int from  = msg.arg1;
                int to = msg.arg2;

                Log.v(TAG, "Move disk " + disk + " from pillar " + from + " to pillar " + to);
            }
        };

        hanoiTask = new HanoiAsyncTask();
        AsyncTaskCompat.executeParallel(hanoiTask, 6);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (hanoiTask != null) {
            hanoiTask.cancel(true);
            hanoiTask = null;
        }

        mainUIHandler.removeCallbacksAndMessages(null);
    }
}
