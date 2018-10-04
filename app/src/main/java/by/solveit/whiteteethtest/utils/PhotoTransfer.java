package by.solveit.whiteteethtest.utils;

import android.util.LongSparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.fotoapparat.result.BitmapPhoto;

public final class PhotoTransfer {

    public static final long INVALID_KEY = -1;

    @Nullable
    private static volatile PhotoTransfer instance;

    @NonNull
    public static PhotoTransfer instance() {
        if (instance == null) synchronized (PhotoTransfer.class) {
            if (instance == null) instance = new PhotoTransfer();
        }
        return instance;
    }

    private long availableId;
    @NonNull
    private LongSparseArray<BitmapPhoto> photos;

    private PhotoTransfer() {
        photos = new LongSparseArray<>();
    }

    public long addPhoto(@NonNull BitmapPhoto photo) {
        photos.append(availableId, photo);
        return availableId++;
    }

    @NonNull
    public BitmapPhoto removePhoto(final long id) {
        BitmapPhoto photo = photos.get(id);
        if (photo == null) throw new RuntimeException("photo = null");
        photos.delete(id);
        return photo;
    }
}
