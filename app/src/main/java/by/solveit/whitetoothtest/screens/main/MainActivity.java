package by.solveit.whitetoothtest.screens.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import by.solveit.whitetoothtest.R;

public class MainActivity extends AppCompatActivity {

    @Nullable
    private MainViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
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
}
