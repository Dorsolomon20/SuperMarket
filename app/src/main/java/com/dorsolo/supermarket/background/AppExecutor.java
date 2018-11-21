package com.dorsolo.supermarket.background;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.dorsolo.supermarket.utilities.Constants;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class provides a utility class for handling different kind of jobs on background threads and on
 * the UI thread/Main thread. It gives a single point of entry to multiple behaviors
 */
public class AppExecutor {

    private static AppExecutor appExecutor;
    private Executor diskIO, networkIO, mainThreadExecutor;

    private AppExecutor(Executor diskIO, Executor networkIO, Executor mainThreadExecutor) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThreadExecutor = mainThreadExecutor;
    }

    /**
     * Acquire an instance of AppExecutor
     *
     * @param nThreads integer value which represent the number of thread that will be in the threadPool of the networkIO executor
     * @return A AppExecutor instance with all executor ready for use
     */
    public static AppExecutor getAppExecutor(int... nThreads) {
        if (nThreads.length == 0) {
            nThreads = new int[1];
            nThreads[0] = Constants.NetworkingConstants.THREAD_POOL_SIZE;
        }
        if (nThreads[0] <= 0 || nThreads[0] > 100)
            throw new IllegalArgumentException("nThreads can't be less or equals to 0 or greater then 100");
        if (appExecutor == null)
            appExecutor = new AppExecutor(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(nThreads[0]), new MainThreadExecutor());
        return appExecutor;
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    public Executor getMainThreadExecutor() {
        return mainThreadExecutor;
    }

    public ExecutorService getExecutorService() {
        return (ExecutorService) diskIO;
    }

    /**
     * Executor on the MainThread
     */
    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler;

        MainThreadExecutor() {
            mainThreadHandler = new Handler();
        }

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
