package by.solveit.whitetoothtest.screens.main;

import android.app.Application;
import android.hardware.camera2.CameraMetadata;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import by.solveit.whitetoothtest.utils.Threads;
import by.solveit.whitetoothtest.utils.camerastream.CameraStream;
import by.solveit.whitetoothtest.utils.camerastream.State;

public class MainViewModel extends AndroidViewModel {

    private static final Integer DEFAULT_CAMERA_LENS_FACING = CameraMetadata.LENS_FACING_FRONT;
    private static final String LOG_TAG = MainViewModel.class.getName();

    @NonNull
    private CameraStream stream;

    public MainViewModel(@NonNull Application application) {
        super(application);
        stream = new CameraStream(application, DEFAULT_CAMERA_LENS_FACING);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void startCamera() {
        stream.start(
                null,
                this::onNewImage,
                Threads.backgroundHandler()
        );
    }

    public void stopCamera() {
        stream.stop();
    }

    @NonNull
    public LiveData<State> getCameraState() {
        return stream.getStateLiveDate();
    }

    private boolean onNewImage(Image image) {
        Log.d(LOG_TAG, "New image was received " + image.toString());
        Image.Plane[] planes = image.getPlanes();
        return false;
    }
}
