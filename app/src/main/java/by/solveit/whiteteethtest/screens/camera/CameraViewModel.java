package by.solveit.whiteteethtest.screens.camera;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import by.solveit.whiteteethtest.screens.preview.PreviewActivity;
import by.solveit.whiteteethtest.utils.PhotoTransfer;
import io.fotoapparat.result.BitmapPhoto;

public final class CameraViewModel extends AndroidViewModel {

    public CameraViewModel(@NonNull Application application) {
        super(application);
    }

    void onPhotoReceived(@NonNull BitmapPhoto photo) {
        PreviewActivity.start(getApplication(), PhotoTransfer.instance().addPhoto(photo));
    }
}
