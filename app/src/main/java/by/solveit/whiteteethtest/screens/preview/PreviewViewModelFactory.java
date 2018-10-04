package by.solveit.whiteteethtest.screens.preview;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import io.fotoapparat.result.BitmapPhoto;

final class PreviewViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static final String LOG_TAG = PreviewViewModelFactory.class.getName();

    @NonNull
    private Application application;
    @NonNull
    private BitmapPhoto photo;

    PreviewViewModelFactory(@NonNull Application application, @NonNull BitmapPhoto photo) {
        super();
        this.application = application;
        this.photo = photo;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (PreviewViewModel.class.equals(modelClass))
                return modelClass.getConstructor(Application.class, BitmapPhoto.class)
                        .newInstance(application, photo);
        } catch (IllegalAccessException | InstantiationException
                | InvocationTargetException | NoSuchMethodException ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
        }
        return super.create(modelClass);
    }
}
