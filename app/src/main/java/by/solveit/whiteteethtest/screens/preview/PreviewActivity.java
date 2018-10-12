package by.solveit.whiteteethtest.screens.preview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import by.solveit.whiteteethtest.R;
import by.solveit.whiteteethtest.utils.PhotoTransfer;
import io.fotoapparat.result.BitmapPhoto;

public final class PreviewActivity extends AppCompatActivity {

    private static final String KEY_PHOTO_ID = "photo_id";

    @BindView(R.id.iv_preview)
    protected ImageView ivPreview;

    @NonNull
    private PreviewViewModel viewModel;

    public static void start(@NonNull Context context, final long photoId) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(KEY_PHOTO_ID, photoId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final long photoId = getIntent().getLongExtra(KEY_PHOTO_ID, PhotoTransfer.INVALID_KEY);
        BitmapPhoto photo = PhotoTransfer.instance().removePhoto(photoId);
        viewModel = ViewModelProviders.of(this,
                new PreviewViewModelFactory(getApplication(), photo)).get(PreviewViewModel.class);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        viewModel.bitmapLiveData().observe(this, ivPreview::setImageBitmap);
        viewModel.messageLiveData().observe(this, this::showMessage);
    }

    private void showMessage(@Nullable Integer messageId) {
        if (messageId != null) Snackbar.make(ivPreview, messageId, Snackbar.LENGTH_LONG).show();
    }
}
