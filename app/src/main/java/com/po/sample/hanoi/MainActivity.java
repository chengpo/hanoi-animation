package com.po.sample.hanoi;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.po.sample.hanoi.databinding.ActivityMainBinding;
import com.po.sample.hanoi.robot.Brain;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int TOTAL_DISK = 10;

    private ActivityMainBinding binding;
    private Handler mainUIHandler;
    private HanoiAsyncTask hanoiTask;

    private class HanoiAsyncTask extends AsyncTask<Integer, Void, Void> {
        int step = 0;

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
                    mainUIHandler.sendMessageDelayed(message, (++step) * 1000) ;
                }

                @Override
                public void onFinished() {
                    Log.i(TAG, "Finish solving puzzle in " + step + " steps!");
                }

                @Override
                public void onCanceled() {
                    Log.i(TAG, "Cancel solving puzzle!");
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.pillar1.loadDisk(TOTAL_DISK);

        final PillarLayout pillarLayouts[] = {
            binding.pillar1,
            binding.pillar2,
            binding.pillar3
        };

        mainUIHandler = new Handler(getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                final int disk = msg.what;
                final int from  = msg.arg1;
                final int to = msg.arg2;

                Log.v(TAG, "Move disk " + disk + " from pillar " + from + " to pillar " + to);

                pillarLayouts[from].removeDisk(disk, from < to,  new PillarLayout.Callback() {
                    @Override
                    public void onDiskRemoved(final DiskView diskView) {
                        pillarLayouts[to].post(new Runnable() {
                            @Override
                            public void run() {
                                pillarLayouts[to].addDisk(diskView);
                            }
                        });
                    }
                });
            }
        };

        mainUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hanoiTask = new HanoiAsyncTask();
                AsyncTaskCompat.executeParallel(hanoiTask, TOTAL_DISK);
            }
        }, 5000);
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
