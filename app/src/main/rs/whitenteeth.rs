#pragma version(1)
#pragma rs java_package_name(by.solveit.whiteteethtest.rs)

const static float kR = 0.2126f;
const static float kG = 0.7152f;
const static float kB = 0.0722f;

int leftBorder;
int rightBorder;
int topBorder;
int bottomBorder;
int toothMinY;
int toothMaxY;
int toothMinU;
int toothMaxU;
int toothMinV;
int toothMaxV;
float gain;

static float getY(const uchar4 in);

static bool isFloatInRange(const float value, const int minValue, const int maxValue);

static bool isIntInRange(const uint32_t value, const int minValue, const int maxValue);

uchar4 RS_KERNEL whitenteeth(const uchar4 in, const uint32_t x, const uint32_t y) {
    uchar4 out = in;
    if (isIntInRange(x, leftBorder, rightBorder) && isIntInRange(y, topBorder, bottomBorder)) {
        const float Y = getY(in);
        const float U = in.b - Y;
        const float V = in.r - Y;
        const float newY = 128;
        const float newR = newY + V;
        const float newG = newY - (V * kR + U * kB) / kG;
        const float newB = newY + U;
        out.r = newR > 255 ? 255 : newR < 0 ? 0 : newR;
        out.g = newG > 255 ? 255 : newG < 0 ? 0 : newG;
        out.b = newB > 255 ? 255 : newB < 0 ? 0 : newB;
    }
    return out;
}

static float getY(const uchar4 in) {
    return in.r * kR + in.g * kG + in.b * kB;
}

static bool isFloatInRange(const float value, const int minValue, const int maxValue) {
    return value >= minValue && value <= maxValue;
}

static bool isIntInRange(const uint32_t value, const int minValue, const int maxValue) {
    return value >= minValue && value <= maxValue;
}
