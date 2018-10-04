package by.solveit.whiteteethtest.screens.preview;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import by.solveit.whiteteethtest.utils.Graphics;
import by.solveit.whiteteethtest.utils.Threads;
import io.fotoapparat.result.BitmapPhoto;

public class PreviewViewModel extends AndroidViewModel {

    @NonNull
    private MutableLiveData<Bitmap> bitmapLiveData;
    @NonNull
    private FaceDetector faceDetector;

    public PreviewViewModel(@NonNull Application application, @NonNull BitmapPhoto photo) {
        super(application);
        bitmapLiveData = new MutableLiveData<>();
        faceDetector = new FaceDetector.Builder(application)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setProminentFaceOnly(true)
                .setTrackingEnabled(false)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();
        Threads.backgroundExecutor().execute(() -> {
            Bitmap rotatedBitmap = rotateBitmap(photo.bitmap, -photo.rotationDegrees);
            photo.bitmap.recycle();
            bitmapLiveData.postValue(rotatedBitmap);
            Rect mouthLocation = getMouthLocation(rotatedBitmap);
            if (mouthLocation != null) {
                Bitmap whitenTeethBitmap = Graphics.instance(application)
                        .whitenTeeth(rotatedBitmap, mouthLocation);
                bitmapLiveData.postValue(whitenTeethBitmap);
                rotatedBitmap.recycle();
            }
        });
    }

    LiveData<Bitmap> bitmapLiveData() {
        return bitmapLiveData;
    }

    @NonNull
    private Bitmap rotateBitmap(@NonNull Bitmap src, final float rotationDegrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationDegrees);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    @Nullable
    private Rect getMouthLocation(@NonNull Bitmap src) {
        SparseArray<Face> faces = faceDetector
                .detect(new Frame.Builder().setBitmap(src).build());
        if (faces.size() == 0) return null;
        Face face = faces.valueAt(0);
        PointF bottomMouth = null;
        PointF leftMouth = null;
        PointF rightMouth = null;
        for (Landmark landmark : face.getLandmarks()) {
            switch (landmark.getType()) {
                case Landmark.BOTTOM_MOUTH:
                    bottomMouth = landmark.getPosition();
                    break;
                case Landmark.LEFT_MOUTH:
                    leftMouth = landmark.getPosition();
                    break;
                case Landmark.RIGHT_MOUTH:
                    rightMouth = landmark.getPosition();
                    break;
            }
        }
        if (bottomMouth != null && leftMouth != null && rightMouth != null) {
            final int left = (int) rightMouth.x;
            final int right = (int) leftMouth.x;
            final int bottom = (int) bottomMouth.y;
            final float middleY = (leftMouth.y + rightMouth.y) / 2;
            final int top = (int) (2 * middleY - bottom);
            return new Rect(left, top, right, bottom);
        }
        return null;
    }
}
