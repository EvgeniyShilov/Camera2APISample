package by.solveit.whiteteethtest.screens.camera;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import by.solveit.whiteteethtest.R;
import by.solveit.whiteteethtest.utils.FotoapparatWrapper;
import io.fotoapparat.view.CameraView;

public final class CameraActivity extends AppCompatActivity {

    @BindView(R.id.cv_preview)
    protected CameraView cvPreview;

    private CameraViewModel viewModel;
    private FotoapparatWrapper fotoapparatWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CameraViewModel.class);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        fotoapparatWrapper = new FotoapparatWrapper(this, cvPreview, getLifecycle());
    }

    @Override
    protected void onDestroy() {
        fotoapparatWrapper.release();
        super.onDestroy();
    }

    @OnClick(R.id.b_take_photo)
    protected void onTakePhotoClick() {
        fotoapparatWrapper.takePhoto(viewModel::onPhotoReceived);
    }
}
