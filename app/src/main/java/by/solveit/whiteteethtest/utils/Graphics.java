package by.solveit.whiteteethtest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import by.solveit.whiteteethtest.rs.ScriptC_whitenteeth;

public final class Graphics {

    private static final int WHITEN_TEETH_MIN_Y = 0;
    private static final int WHITEN_TEETH_MAX_Y = 255;
    private static final int WHITEN_TEETH_MIN_U = -32;
    private static final int WHITEN_TEETH_MAX_U = -2;
    private static final int WHITEN_TEETH_MIN_V = 0;
    private static final int WHITEN_TEETH_MAX_V = 28;
    private static final float WHITEN_TEETH_GAIN = 1.2f;

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
        whitenTeethScript.set_toothMinY(WHITEN_TEETH_MIN_Y);
        whitenTeethScript.set_toothMaxY(WHITEN_TEETH_MAX_Y);
        whitenTeethScript.set_toothMinU(WHITEN_TEETH_MIN_U);
        whitenTeethScript.set_toothMaxU(WHITEN_TEETH_MAX_U);
        whitenTeethScript.set_toothMinV(WHITEN_TEETH_MIN_V);
        whitenTeethScript.set_toothMaxV(WHITEN_TEETH_MAX_V);
        whitenTeethScript.set_gain(WHITEN_TEETH_GAIN);
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
        whitenTeethScript.forEach_whitenteeth(srcAllocation, dstAllocation);
        dstAllocation.copyTo(dst);
        return dst;
    }
}
