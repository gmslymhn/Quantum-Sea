package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: FVector2D
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:04
 * @Version: 1.0
 **/
public class FVector2D {
    public float X;
    public float Y;

    public String toString() {
        return String.format("(X=%f, Y=%f)", X, Y);
    }

    public FVector2D() {
        this(0, 0);
    }

    public FVector2D(float x, float y) {
        this.X = x;
        this.Y = y;
    }

    public boolean isZero() {
        return (X > -0.1f && X < 0.1f && Y > -0.1f && Y < 0.1f);
    }

    public FVector2D add(FVector2D other) {
        return new FVector2D(X + other.X, Y + other.Y);
    }

    public FVector2D subtract(FVector2D other) {
        return new FVector2D(X - other.X, Y - other.Y);
    }

    public FVector2D multiply(float scalar) {
        return new FVector2D(X * scalar, Y * scalar);
    }

    public FVector2D multiply(FVector2D other) {
        return new FVector2D(X * other.X, Y * other.Y);
    }

    public FVector2D divide(float scalar) {
        return new FVector2D(X / scalar, Y / scalar);
    }

    public FVector2D divide(FVector2D other) {
        return new FVector2D(X / other.X, Y / other.Y);
    }

    public FVector2D assign(FVector2D other) {
        this.X = other.X;
        this.Y = other.Y;
        return this;
    }

    public FVector2D addAssign(FVector2D other) {
        this.X += other.X;
        this.Y += other.Y;
        return this;
    }

    public FVector2D subtractAssign(FVector2D other) {
        this.X -= other.X;
        this.Y -= other.Y;
        return this;
    }

    public FVector2D multiplyAssign(float other) {
        this.X *= other;
        this.Y *= other;
        return this;
    }

    public boolean isValid() {
        return -65535.f < X && X < 65535.f && -65535.f < Y && Y < 65535.f;
    }

    public float distance(FVector2D other) {
        float a = (X - other.X);
        float b = (Y - other.Y);
        return (float) Math.sqrt(a * a + b * b);
    }
}
