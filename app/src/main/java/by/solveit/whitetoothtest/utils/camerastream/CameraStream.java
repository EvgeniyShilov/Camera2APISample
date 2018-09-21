package by.solveit.whitetoothtest.utils.camerastream;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

public class CameraStream {

    private static final int IMAGE_FORMAT = ImageFormat.YUV_420_888;
    private static final int READER_MAX_IMAGES_COUNT = 16;
    private static final int TEMPLATE_TYPE = CameraDevice.TEMPLATE_PREVIEW;
    private static final String LOG_TAG = CameraStream.class.getName();

    private boolean started;
    private int lensFacing;
    @NonNull
    private CameraManager cameraManager;
    @Nullable
    private CameraManager.AvailabilityCallback callback;
    @Nullable
    private ImageReader reader;
    @Nullable
    private CameraDevice device;
    @Nullable
    private CameraCaptureSession session;

    public CameraStream(@NonNull Context context, final int lensFacing) {
        cameraManager = getCameraManager(context);
        this.lensFacing = lensFacing;
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public synchronized boolean start(
            @Nullable Size requiredSize,
            @NonNull StateListener stateListener,
            @NonNull ImageListener imageListener,
            @NonNull Handler handler
    ) {
        if (started) return true;
        String cameraId = getCameraId(lensFacing);
        started = cameraId != null;
        if (started) {
            this.callback = new CameraAvailabilityCallback(
                    cameraId,
                    requiredSize,
                    stateListener,
                    imageListener,
                    handler
            );
            cameraManager.registerAvailabilityCallback(this.callback, handler);
        }
        return started;
    }

    public synchronized void stop() {
        if (callback != null) {
            cameraManager.unregisterAvailabilityCallback(callback);
            callback = null;
        }
        if (reader != null) {
            reader.close();
            reader = null;
        }
        if (device != null) {
            device.close();
            device = null;
        }
        if (session != null) {
            session.close();
            session = null;
        }
        started = false;
    }

    @NonNull
    private CameraManager getCameraManager(@NonNull Context context) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) throw new RuntimeException("manager = null");
        return manager;
    }

    @Nullable
    private String getCameraId(int requiredFacing) {
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            for (String cameraId : cameraIdList) {
                try {
                    CameraCharacteristics characteristics =
                            cameraManager.getCameraCharacteristics(cameraId);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing == null || requiredFacing != facing) continue;
                    StreamConfigurationMap configs = characteristics
                            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (configs == null) continue;
                    int[] formats = configs.getOutputFormats();
                    for (final int format : formats) if (format == IMAGE_FORMAT) return cameraId;
                } catch (CameraAccessException ex) {
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                }
            }
            return null;
        } catch (CameraAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private class CameraAvailabilityCallback extends CameraManager.AvailabilityCallback {

        @NonNull
        private String cameraId;
        @Nullable
        private Size requiredSize;
        @NonNull
        private StateListener stateListener;
        @NonNull
        private ImageListener imageListener;
        @NonNull
        private Handler handler;

        CameraAvailabilityCallback(
                @NonNull String cameraId,
                @Nullable Size requiredSize,
                @NonNull StateListener stateListener,
                @NonNull ImageListener imageListener,
                @NonNull Handler handler
        ) {
            super();
            this.cameraId = cameraId;
            this.stateListener = stateListener;
            this.requiredSize = requiredSize;
            this.imageListener = imageListener;
            this.handler = handler;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCameraAvailable(@NonNull String availableCameraId) {
            super.onCameraAvailable(availableCameraId);
            if (cameraId.equals(availableCameraId)) {
                try {
                    stateListener.onStateChanged(State.LOADING);
                    if (reader != null) reader.close();
                    reader = getReaderForCamera(cameraId, requiredSize);
                    reader.setOnImageAvailableListener(reader -> {
                        Image image = reader.acquireLatestImage();
                        if (!imageListener.onNewImage(image)) image.close();
                    }, handler);
                    cameraManager.openCamera(
                            cameraId,
                            new CameraStateCallback(stateListener, handler),
                            handler
                    );
                } catch (CameraAccessException ex) {
                    Log.e(LOG_TAG, ex.getMessage(), ex);
                }
            }
        }

        @Override
        public void onCameraUnavailable(@NonNull String unavailableCameraId) {
            super.onCameraUnavailable(unavailableCameraId);
            if (cameraId.equals(unavailableCameraId))
                stateListener.onStateChanged(State.UNAVAILABLE);
        }

        @NonNull
        private ImageReader getReaderForCamera(
                @NonNull String cameraId,
                @Nullable Size requiredSize
        ) throws CameraAccessException {
            CameraCharacteristics cameraCharacteristics =
                    cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap configs = cameraCharacteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (configs == null) throw new RuntimeException("configs = null");
            Size[] sizes = configs.getOutputSizes(IMAGE_FORMAT);
            //TODO: choose size
            Size size = sizes[0];
            return ImageReader.newInstance(
                    size.getWidth(),
                    size.getHeight(),
                    IMAGE_FORMAT,
                    READER_MAX_IMAGES_COUNT
            );
        }
    }

    private class CameraStateCallback extends CameraDevice.StateCallback {

        @NonNull
        private StateListener stateListener;
        @NonNull
        private Handler handler;

        CameraStateCallback(@NonNull StateListener stateListener, @NonNull Handler handler) {
            super();
            this.stateListener = stateListener;
            this.handler = handler;
        }

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            try {
                if (device != null) device.close();
                device = camera;
                Surface surface = reader.getSurface();
                CaptureRequest.Builder builder = camera.createCaptureRequest(TEMPLATE_TYPE);
                builder.addTarget(surface);
                camera.createCaptureSession(
                        Collections.singletonList(surface),
                        new SessionStateCallback(builder.build(), stateListener, handler),
                        handler
                );
            } catch (CameraAccessException ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.e(LOG_TAG, "Camera " + camera.getId() + " was disconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(LOG_TAG, "Error occurred, camera id = " + camera.getId() +
                    ", error code = " + error);
        }
    }

    private class SessionStateCallback extends CameraCaptureSession.StateCallback {

        @NonNull
        private CaptureRequest request;
        @NonNull
        private StateListener stateListener;
        @NonNull
        private Handler handler;

        SessionStateCallback(
                @NonNull CaptureRequest request,
                @NonNull StateListener stateListener,
                @NonNull Handler handler
        ) {
            super();
            this.request = request;
            this.stateListener = stateListener;
            this.handler = handler;
        }

        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                if (CameraStream.this.session != null) CameraStream.this.session.close();
                CameraStream.this.session = session;
                session.setRepeatingRequest(request, null, handler);
                stateListener.onStateChanged(State.AVAILABLE);
            } catch (CameraAccessException ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            throw new RuntimeException("session configuration failed");
        }
    }
}
