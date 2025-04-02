package com.flxpoint.troubleshooting;

import java.util.*;
import java.util.concurrent.*;

public class TroubleshootingTest {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    private static final Map<String, Integer> sharedData = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        TroubleshootingTest test = new TroubleshootingTest();
        test.runTest();
        executor.shutdown();
    }

    public void runTest() {
        List<Future<Integer>> futures = new ArrayList<>();
        
        futures.add(executor.submit(() -> updateSharedData("key1")));
        futures.add(executor.submit(() -> updateSharedData("key1")));
        futures.add(executor.submit(this::causeNullPointer));
        futures.add(executor.submit(() -> parseInteger("ABC")));

        Object lock1 = new Object();
        Object lock2 = new Object();
        
        executor.submit(() -> deadlockMethod(lock1, lock2));
        executor.submit(() -> deadlockMethod(lock2, lock1));
        
        executor.submit(() -> {
            try {
                methodThrowsException();
            } catch (Exception e) {
                System.out.println("Exception caught: " + e.getMessage());
            }
        });

        executor.submit(this::infiniteLoop);
        executor.submit(this::unclosedScanner);
    }

    private Integer updateSharedData(String key) {
        return sharedData.merge(key, 1, Integer::sum);
    }

    private Integer causeNullPointer() {
        String value = null;
        return (value != null) ? value.length() : -1;
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + value);
            return -1;
        }
    }

    private void deadlockMethod(Object obj1, Object obj2) {
        Object firstLock = obj1.hashCode() < obj2.hashCode() ? obj1 : obj2;
        Object secondLock = obj1.hashCode() < obj2.hashCode() ? obj2 : obj1;

        synchronized (firstLock) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}

            synchronized (secondLock) {
                System.out.println("Acquired both locks safely");
            }
        }
    }

    private void infiniteLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void unclosedScanner() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter something: ");
            String input = scanner.nextLine();
            System.out.println("You entered: " + input);
        }
    }

    private void methodThrowsException() throws Exception {
        throw new Exception("Test Exception");
    }
}
