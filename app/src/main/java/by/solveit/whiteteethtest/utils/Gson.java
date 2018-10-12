package by.solveit.whiteteethtest.utils;

import com.google.gson.GsonBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class Gson {

    @Nullable
    private static volatile com.google.gson.Gson instance;

    @NonNull
    public static com.google.gson.Gson instance() {
        if (instance == null) synchronized (Gson.class) {
            if (instance == null) instance = new GsonBuilder().create();
        }
        return instance;
    }
}
