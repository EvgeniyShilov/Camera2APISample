package by.solveit.whiteteethtest.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import by.solveit.whiteteethtest.R;
import by.solveit.whiteteethtest.models.Pixel;

public final class Clusters {

    public enum Type {

        TEETH(R.raw.teeth, new Pixel(0.6758f, -0.0083f, 0.0121f, 0.4048f, 0.3726f)),
        LIPS(R.raw.lips, new Pixel(0.4902f, 0.0025f, 0.0972f, 0.5177f, 0.4786f)),
        SKIN(R.raw.skin, new Pixel(0.6661f, 0.0024f, 0.0470f, 0.6331f, 0.6729f)),
        MOUTH(R.raw.mouth, new Pixel(0.1556f, -0.0022f, 0.0326f, 0.5746f, 0.2305f));

        @RawRes
        private int rawRes;
        @NonNull
        private Pixel averagePixel;

        Type(@RawRes int rawRes, @NonNull Pixel averagePixel) {
            this.rawRes = rawRes;
            this.averagePixel = averagePixel;
        }

        @NonNull
        public Pixel getAveragePixel() {
            return averagePixel;
        }
    }

    @NonNull
    public static Pixel[] getCluster(@NonNull Context context, @NonNull Type type) {
        InputStream stream = context.getResources().openRawResource(type.rawRes);
        Reader reader = new BufferedReader(new InputStreamReader(stream));
        return Gson.instance().fromJson(reader, Pixel[].class);
    }
}
