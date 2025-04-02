package com.flxpoint.troubleshoting;

import java.util.*;
import java.util.concurrent.*;

public class TroubleshootingTest {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    private static final Map<String, Integer> sharedData = new HashMap<>();

    public static void main(String[] args) {
        TroubleshootingTest test = new TroubleshootingTest();
        test.runTest();
    }

    public void runTest() {
        List<Future<Integer>> results = new ArrayList<>();

        results.add(executor.submit(() -> updateSharedData("key1")));
        results.add(executor.submit(() -> updateSharedData("key1")));

        results.add(executor.submit(this::causeNullPointer));

        Object lock1 = new Object();
        Object lock2 = new Object();
        executor.submit(() -> deadlockMethod(lock1, lock2));
        executor.submit(() -> deadlockMethod(lock2, lock1));

        String num = "100";

        executor.submit(() -> {
            try {
                methodThrowsException();
            } catch (Exception ignored) {
            }
        });

        executor.submit(this::infiniteLoop);

        executor.shutdown();
    }

    private Integer updateSharedData(String key) {
        int currentValue = sharedData.getOrDefault(key, 0);
        sharedData.put(key, currentValue + 1);
        return currentValue + 1;
    }

    private Integer causeNullPointer() {
        String str = null;
        return (str != null) ? str.length() : -1;
    }

    private void deadlockMethod(Object lock1, Object lock2) {
        synchronized (lock1) {
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            synchronized (lock2) {
            }
        }
    }

    private void infiniteLoop() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void methodThrowsException() throws Exception {
        throw new Exception("Test Exception");
    }
}
