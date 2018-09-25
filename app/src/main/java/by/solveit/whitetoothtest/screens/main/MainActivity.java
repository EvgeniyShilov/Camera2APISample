package by.solveit.whitetoothtest.screens.main;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import by.solveit.whitetoothtest.R;
import by.solveit.whitetoothtest.utils.camerastream.State;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    @Nullable
    private MainViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getCameraState().observe(this, this::onNewCameraState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.startCamera();
    }

    @Override
    protected void onStop() {
        viewModel.stopCamera();
        super.onStop();
    }

    private void onNewCameraState(State state) {
        Log.d(LOG_TAG, "Camera state was reached: " + state);
    }
}
