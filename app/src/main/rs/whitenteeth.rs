#pragma version(1)
#pragma rs java_package_name(by.solveit.whiteteethtest.rs)

const static float K_R = 0.2126f;
const static float K_G = 0.7152f;
const static float K_B = 0.0722f;

typedef struct Pixel {
    float y;
    float u;
    float v;
    float dx;
    float dy;
} Pixel_t;

float gain;
float yFactor;
float uFactor;
float vFactor;
float dxFactor;
float dyFactor;
Pixel_t averageTeeth;
Pixel_t averageLips;
Pixel_t averageSkin;
Pixel_t averageMouth;

int leftBorder;
int rightBorder;
int topBorder;
int bottomBorder;
int xCenter;
int yCenter;
float halfWidth;
float halfHeight;

static float getDistance(
    const float y,
    const float u,
    const float v,
    const float dx,
    const float dy,
    const Pixel_t pixel
);

static float getY(const uchar4 in);

static float getU(const float B, const float Y);

static float getV(const float R, const float Y);

static bool isFloatInRange(const float value, const int minValue, const int maxValue);

static bool isIntInRange(const int value, const int minValue, const int maxValue);

uchar4 RS_KERNEL whitenteeth(const uchar4 in, const int x, const int y) {
    uchar4 out = in;
    if (isIntInRange(x, leftBorder, rightBorder) && isIntInRange(y, topBorder, bottomBorder)) {
        const float Y = getY(in);
        const float u = getU(in.b / 256.0f, Y);
        const float v = getV(in.r / 256.0f, Y);
        const float dx = abs(x - xCenter) / halfWidth;
        const float dy = abs(y - yCenter) / halfHeight;
        const float distTeeth = getDistance(Y, u, v, dx, dy, averageTeeth);
        const float distLips = getDistance(Y, u, v, dx, dy, averageLips);
        const float distSkin = getDistance(Y, u, v, dx, dy, averageSkin);
        const float distMouth = getDistance(Y, u, v, dx, dy, averageMouth);
        if (distTeeth <= distLips && distTeeth <= distSkin && distTeeth <= distMouth) {
            float newY = Y * gain * 256;
            newY = newY > 255 ? 255 : newY;
            out.r = newY;
            out.g = newY;
            out.b = newY;
        }
    }
    return out;
}

static float getDistance(
    const float y,
    const float u,
    const float v,
    const float dx,
    const float dy,
    const Pixel_t pixel
) {
    const float distY = (y - pixel.y) * yFactor;
    const float distU = (u - pixel.u) * uFactor;
    const float distV = (v - pixel.v) * vFactor;
    const float distDx = (dx - pixel.dx) * dxFactor;
    const float distDy = (dy - pixel.dy) * dyFactor;
    return sqrt(pow(distY, 2) + pow(distU, 2) + pow(distV, 2) + pow(distDx, 2) + pow(distDy, 2));
}

static float getY(const uchar4 in) {
    return (in.r * K_R + in.g * K_G + in.b * K_B) / 256;
}

static float getU(const float b, const float y) {
    return 0.5f * (b - y) / (1 - K_B);
}

static float getV(const float r, const float y) {
    return 0.5f * (r - y) / (1 - K_R);
}

static bool isFloatInRange(const float value, const int minValue, const int maxValue) {
    return value >= minValue && value <= maxValue;
}

static bool isIntInRange(const int value, const int minValue, const int maxValue) {
    return value >= minValue && value <= maxValue;
}
