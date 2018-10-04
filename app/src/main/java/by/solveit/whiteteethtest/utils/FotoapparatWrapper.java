package by.solveit.whiteteethtest.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.SelectorsKt;
import io.fotoapparat.view.CameraRenderer;

public final class FotoapparatWrapper implements LifecycleObserver {

    @NonNull
    private Lifecycle lifecycle;
    @NonNull
    private Fotoapparat fotoapparat;

    public FotoapparatWrapper(@NonNull Context context,
                              @NonNull CameraRenderer cameraRenderer,
                              @NonNull Lifecycle lifecycle) {
        fotoapparat = Fotoapparat.with(context.getApplicationContext())
                .into(cameraRenderer)
                .lensPosition(SelectorsKt.firstAvailable(
                        LensPositionSelectorsKt.front(),
                        LensPositionSelectorsKt.back(),
                        LensPositionSelectorsKt.external()
                ))
                .build();
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    public void release() {
        lifecycle.removeObserver(this);
    }

    public void takePhoto(@NonNull Callback callback) {
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED))
            fotoapparat.takePicture()
                    .toBitmap()
                    .whenAvailable(photo -> {
                        callback.onPictureTaken(photo);
                        return null;
                    });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void start() {
        fotoapparat.start();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void stop() {
        fotoapparat.stop();
    }

    public interface Callback {

        void onPictureTaken(@NonNull BitmapPhoto photo);
    }
}
