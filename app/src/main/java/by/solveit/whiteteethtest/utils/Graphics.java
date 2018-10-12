package by.solveit.whiteteethtest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import by.solveit.whiteteethtest.models.Pixel;
import by.solveit.whiteteethtest.rs.ScriptC_whitenteeth;
import by.solveit.whiteteethtest.rs.ScriptField_Pixel;

public final class Graphics {

    private static final float WHITEN_TEETH_GAIN = 1.125f;
    private static final float WHITEN_TEETH_Y_FACTOR = 1.0f;
    private static final float WHITEN_TEETH_U_FACTOR = 2.3585f;
    private static final float WHITEN_TEETH_V_FACTOR = 2.1340f;
    private static final float WHITEN_TEETH_DX_FACTOR = 0.2f;
    private static final float WHITEN_TEETH_DY_FACTOR = 0.2f;

    @Nullable
    private volatile static Graphics instance;

    @NonNull
    public static Graphics instance(@NonNull Context context) {
        if (instance == null) synchronized (Graphics.class) {
            if (instance == null) instance = new Graphics(context.getApplicationContext());
        }
        return instance;
    }

    @NonNull
    private RenderScript renderScript;
    @NonNull
    private ScriptC_whitenteeth whitenTeethScript;

    private Graphics(@NonNull Context context) {
        renderScript = RenderScript.create(context);
        whitenTeethScript = new ScriptC_whitenteeth(renderScript);
        whitenTeethScript.set_gain(WHITEN_TEETH_GAIN);
        whitenTeethScript.set_yFactor(WHITEN_TEETH_Y_FACTOR);
        whitenTeethScript.set_uFactor(WHITEN_TEETH_U_FACTOR);
        whitenTeethScript.set_vFactor(WHITEN_TEETH_V_FACTOR);
        whitenTeethScript.set_dxFactor(WHITEN_TEETH_DX_FACTOR);
        whitenTeethScript.set_dyFactor(WHITEN_TEETH_DY_FACTOR);
        whitenTeethScript.set_averageTeeth(getAveragePixel(Clusters.Type.TEETH));
        whitenTeethScript.set_averageLips(getAveragePixel(Clusters.Type.LIPS));
        whitenTeethScript.set_averageSkin(getAveragePixel(Clusters.Type.SKIN));
        whitenTeethScript.set_averageMouth(getAveragePixel(Clusters.Type.MOUTH));
    }

    @NonNull
    public Bitmap whitenTeeth(@NonNull Bitmap src, @NonNull Rect mouthLocation) {
        Allocation srcAllocation = Allocation.createFromBitmap(renderScript, src);
        Bitmap dst = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Allocation dstAllocation = Allocation.createFromBitmap(renderScript, dst);
        whitenTeethScript.set_leftBorder(mouthLocation.left);
        whitenTeethScript.set_topBorder(mouthLocation.top);
        whitenTeethScript.set_rightBorder(mouthLocation.right);
        whitenTeethScript.set_bottomBorder(mouthLocation.bottom);
        whitenTeethScript.set_xCenter(mouthLocation.centerX());
        whitenTeethScript.set_yCenter(mouthLocation.centerY());
        whitenTeethScript.set_halfWidth(mouthLocation.width() / 2f);
        whitenTeethScript.set_halfHeight(mouthLocation.height() / 2f);
        whitenTeethScript.forEach_whitenteeth(srcAllocation, dstAllocation);
        dstAllocation.copyTo(dst);
        return dst;
    }

    @NonNull
    private ScriptField_Pixel.Item getAveragePixel(@NonNull Clusters.Type type) {
        Pixel pixel = type.getAveragePixel();
        ScriptField_Pixel pointer = new ScriptField_Pixel(renderScript, 1);
        pointer.set_y(0, pixel.getY(), false);
        pointer.set_u(0, pixel.getU(), false);
        pointer.set_v(0, pixel.getV(), false);
        pointer.set_dx(0, pixel.getDx(), false);
        pointer.set_dy(0, pixel.getDy(), false);
        pointer.copyAll();
        return pointer.get(0);
    }
}
