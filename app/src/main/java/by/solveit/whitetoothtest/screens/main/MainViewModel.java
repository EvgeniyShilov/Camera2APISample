package by.solveit.whitetoothtest.screens.main;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraMetadata;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import by.solveit.whitetoothtest.utils.Threads;
import by.solveit.whitetoothtest.utils.camerastream.CameraStream;
import by.solveit.whitetoothtest.utils.camerastream.State;

public class MainViewModel extends AndroidViewModel implements LifecycleObserver {

    private static final Integer DEFAULT_CAMERA_LENS_FACING = CameraMetadata.LENS_FACING_FRONT;
    private static final String LOG_TAG = MainViewModel.class.getName();

    @NonNull
    private CameraStream stream;
    @NonNull
    private MutableLiveData<String[]> permissionsRequestLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        stream = new CameraStream(application, DEFAULT_CAMERA_LENS_FACING);
        permissionsRequestLiveData = new MutableLiveData<>();
    }

    @NonNull
    LiveData<State> getCameraState() {
        return stream.getStateLiveDate();
    }

    @NonNull
    LiveData<String[]> getPermissionsRequestLiveData() {
        return permissionsRequestLiveData;
    }

    void grantPermission(@NonNull String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                tryToStartCamera();
                break;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void tryToStartCamera() {
        if (ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) startCamera();
        else permissionsRequestLiveData.postValue(new String[]{Manifest.permission.CAMERA});
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void startCamera() throws SecurityException {
        stream.start(
                null,
                this::onNewImage,
                Threads.backgroundHandler()
        );
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void stopCamera() {
        stream.stop();
    }

    private boolean onNewImage(@NonNull Image image) {
        Log.d(LOG_TAG, "New image was received " + image.toString());
        Image.Plane[] planes = image.getPlanes();
        return false;
    }
}
