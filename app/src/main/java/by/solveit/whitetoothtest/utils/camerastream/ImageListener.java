package by.solveit.whitetoothtest.utils.camerastream;

import android.media.Image;

import androidx.annotation.NonNull;

public interface ImageListener {

    boolean onNewImage(@NonNull Image image);
}
