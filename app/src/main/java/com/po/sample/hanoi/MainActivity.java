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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int TOTAL_DISK = 10;

    private static final int MSG_APPEND_DISK_MOVEMENT = 1;
    private static final int MSG_POP_DISK_MOVEMENT = 2;

    private ActivityMainBinding binding;
    private Handler mainUIHandler;
    private HanoiAsyncTask hanoiTask;

    private static class DiskMovement {
        int disk;
        int from;
        int to;

        public DiskMovement(int disk, int from, int to) {
            this.disk = disk;
            this.from = from;
            this.to = to;
        }
    }

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

                    Message message = mainUIHandler.obtainMessage(MSG_APPEND_DISK_MOVEMENT, 0, 0, new DiskMovement(disk, from, to));
                    mainUIHandler.sendMessage(message) ;
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
            private ArrayList<DiskMovement> movements = new ArrayList<>();

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_APPEND_DISK_MOVEMENT: {
                        final int disk = msg.what;
                        final int from  = msg.arg1;
                        final int to = msg.arg2;

                        Log.v(TAG, "Move disk " + disk + " from pillar " + from + " to pillar " + to);
                        movements.add((DiskMovement) msg.obj);
                        if (movements.size() == 1) {
                            Message message = mainUIHandler.obtainMessage(MSG_POP_DISK_MOVEMENT);
                            mainUIHandler.sendMessage(message);
                        }
                    }
                    break;

                    case MSG_POP_DISK_MOVEMENT: {
                        if (movements.isEmpty()) {
                            break;
                        }

                        DiskMovement movement = movements.remove(0);

                        final int disk = movement.disk;
                        final int from  = movement.from;
                        final int to = movement.to;

                        pillarLayouts[from].removeDisk(disk, new PillarLayout.Callback() {
                            @Override
                            public void onDiskMoved(DiskView diskView) {
                                pillarLayouts[to].addDisk(diskView, new PillarLayout.Callback() {
                                    @Override
                                    public void onDiskMoved(DiskView disk) {
                                        Message message = mainUIHandler.obtainMessage(MSG_POP_DISK_MOVEMENT);
                                        mainUIHandler.sendMessage(message);
                                    }
                                });
                            }
                        });
                    }
                    break;
                }
            }
        };

        mainUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hanoiTask = new HanoiAsyncTask();
                AsyncTaskCompat.executeParallel(hanoiTask, TOTAL_DISK);
            }
        }, 3000);
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
