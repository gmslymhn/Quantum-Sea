package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: FVector_class
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:04
 * @Version: 1.0
 **/
public class FVector_class {
    public float X;
    public float Y;
    public float Z;

    public String toString() {
        return String.format("(X=%f, Y=%f, Z=%f)", X, Y, Z);
    }

    public FVector_class() {
        this(0.0f, 0.0f, 0.0f);
    }

    public FVector_class(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public float dotProduct(FVector_class v) {
        return (X * v.X) + (Y * v.Y) + (Z * v.Z);
    }

    public FVector_class crossProduct(FVector_class v) {
        return new FVector_class(
                (Y * v.Z) - (Z * v.Y),
                (Z * v.X) - (X * v.Z),
                (X * v.Y) - (Y * v.X)
        );
    }

    public FVector_class min(FVector_class v) {
        return new FVector_class(
                Math.min(X, v.X),
                Math.min(Y, v.Y),
                Math.min(Z, v.Z)
        );
    }

    public FVector_class max(FVector_class v) {
        return new FVector_class(
                Math.max(X, v.X),
                Math.max(Y, v.Y),
                Math.max(Z, v.Z)
        );
    }

    public boolean equals(FVector_class v) {
        return X == v.X && Y == v.Y && Z == v.Z;
    }

    public FVector_class negate() {
        return new FVector_class(-X, -Y, -Z);
    }

    public FVector_class add(FVector_class v) {
        return new FVector_class(X + v.X, Y + v.Y, Z + v.Z);
    }

    public FVector_class subtract(FVector_class v) {
        return new FVector_class(X - v.X, Y - v.Y, Z - v.Z);
    }

    public FVector_class multiply(FVector_class v) {
        return new FVector_class(X * v.X, Y * v.Y, Z * v.Z);
    }

    public FVector_class multiply(float value) {
        return new FVector_class(X * value, Y * value, Z * value);
    }

    public FVector_class getNormalizedVector() {
        float length = length();
        if (length == 0) return new FVector_class();
        return multiply(1.0f / length);
    }

    public void normalize() {
        FVector_class normalized = getNormalizedVector();
        this.X = normalized.X;
        this.Y = normalized.Y;
        this.Z = normalized.Z;
    }

    public float length() {
        return (float) Math.sqrt(X * X + Y * Y + Z * Z);
    }

    public float distance(FVector_class v) {
        return subtract(v).length();
    }

    public FVector_class getSignVector() {
        return new FVector_class(
                X >= 0 ? 1.0f : -1.0f,
                Y >= 0 ? 1.0f : -1.0f,
                Z >= 0 ? 1.0f : -1.0f
        );
    }

    public boolean isNearlyZero(float tolerance) {
        return Math.abs(X) <= tolerance && Math.abs(Y) <= tolerance && Math.abs(Z) <= tolerance;
    }

    // 静态乘法重载
    public static FVector_class multiply(float value, FVector_class v) {
        return v.multiply(value);
    }
}
