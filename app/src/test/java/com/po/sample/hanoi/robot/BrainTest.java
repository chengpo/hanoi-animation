package com.po.sample.hanoi.robot;

import android.support.v4.os.CancellationSignal;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BrainTest {
    @Test
    public void testSolvingHanoi() {
        final int totalDisk = 6;

        final ArrayList<ArrayList<Integer>> pillars = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            pillars.add(new ArrayList<Integer>());
        }

        for(int i = 0; i < totalDisk; i++) {
            pillars.get(0).add(i);
        }

        Brain.think(6, new Brain.Callback() {
            @Override
            public void onMoveDisk(int disk, int from, int to) {
                ArrayList<Integer> pillarFrom = pillars.get(from);
                assertEquals("disk id does not match", disk, pillarFrom.get(0).intValue());

                ArrayList<Integer> pillarTo = pillars.get(to);

                if (!pillarTo.isEmpty()) {
                    int topDisk = pillarTo.get(0);
                    assertTrue("disk id should less then the top disk on destination pillar", topDisk > disk);
                }

                pillarFrom.remove(0);
                pillarTo.add(0, disk);
            }

            @Override
            public void onFinished() {
                assertTrue("pillar 1 should be empty", pillars.get(0).isEmpty());
                assertEquals("pillar 3 should have " + totalDisk + " disks", totalDisk,  pillars.get(2).size());
            }

            @Override
            public void onCanceled() {
            }
        }, new CancellationSignal());
    }
}
