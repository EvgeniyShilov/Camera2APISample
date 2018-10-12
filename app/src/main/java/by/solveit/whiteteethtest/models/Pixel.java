package by.solveit.whiteteethtest.models;

public final class Pixel {

    private float y;
    private float u;
    private float v;
    private float dx;
    private float dy;

    public Pixel(final float y, final float u, final float v, final float dx, final float dy) {
        this.y = y;
        this.u = u;
        this.v = v;
        this.dx = dx;
        this.dy = dy;
    }

    public float getY() {
        return y;
    }

    public float getU() {
        return u;
    }

    public float getV() {
        return v;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }
}
