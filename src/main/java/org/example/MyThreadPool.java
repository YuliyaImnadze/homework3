package org.example;

import java.util.LinkedList;
import java.util.List;

public class MyThreadPool {

    private final List<Worker> workers;
    private final LinkedList<Runnable> taskQueue;
    private volatile boolean isShutdown = false;

    public MyThreadPool(int poolSize) {
        if (poolSize <= 0) throw new IllegalArgumentException("Pool size must be > 0");
        this.taskQueue = new LinkedList<>();
        this.workers = new LinkedList<>();

        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            worker.start();
        }
    }

    public void execute(Runnable task) {
        synchronized (taskQueue) {
            if (isShutdown) {
                throw new IllegalStateException("ThreadPool is shutdown.");
            }
            taskQueue.addLast(task);
            taskQueue.notify();
        }
    }

    public void shutdown() {
        synchronized (taskQueue) {
            isShutdown = true;
            taskQueue.notifyAll();
        }
    }

    public void awaitTermination() {
        for (Worker worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                Runnable task;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() && !isShutdown) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    if (taskQueue.isEmpty() && isShutdown) {
                        break;
                    }
                    task = taskQueue.removeFirst();
                }

                try {
                    task.run();
                } catch (RuntimeException e) {
                    System.err.println("Task error: " + e.getMessage());
                }
            }
        }
    }
}
