package by.solveit.whitetoothtest.screens.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import by.solveit.whitetoothtest.R;
import by.solveit.whitetoothtest.utils.camerastream.State;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 84;
    private static final String LOG_TAG = MainActivity.class.getName();

    @Nullable
    private MainViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getCameraState().observe(this, this::onNewCameraState);
        viewModel.getPermissionsRequestLiveData().observe(this, this::requestPermissions);
        getLifecycle().addObserver(viewModel);
    }

    @Override
    protected void onDestroy() {
        getLifecycle().removeObserver(viewModel);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE)
            for (int i = 0; i < permissions.length; i++)
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    viewModel.grantPermission(permissions[i]);
    }

    private void requestPermissions(@Nullable String[] permissions) {
        if (permissions != null)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE);
    }

    private void onNewCameraState(@Nullable State state) {
        if (state != null)
            Log.d(LOG_TAG, "Camera state was reached: " + state);
    }
}
