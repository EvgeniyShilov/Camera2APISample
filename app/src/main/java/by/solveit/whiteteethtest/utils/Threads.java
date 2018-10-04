package by.solveit.whiteteethtest.utils;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class Threads {

    private static final String BACKGROUND_THREAD_NAME = "background_thread";
    private static final Object BACKGROUND_THREAD_INIT_LOCK = new Object();

    @Nullable
    private static volatile Handler backgroundHandler;

    @NonNull
    public static Handler backgroundHandler() {
        if (backgroundHandler == null) synchronized (BACKGROUND_THREAD_INIT_LOCK) {
            if (backgroundHandler == null) {
                HandlerThread handlerThread = new HandlerThread(BACKGROUND_THREAD_NAME);
                handlerThread.start();
                backgroundHandler = new Handler(handlerThread.getLooper());
            }
        }
        return backgroundHandler;
    }

    @NonNull
    public static Executor backgroundExecutor() {
        return runnable -> backgroundHandler().post(runnable);
    }
}
