package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: FRotator_class
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:08
 * @Version: 1.0
 **/
public class FRotator_class {
    public float Pitch;
    public float Yaw;
    public float Roll;

    @Override
    public String toString() {
        return String.format("(pitch=%.2f, yaw=%.2f, roll=%.2f)", Pitch, Yaw, Roll);
    }

    public FRotator_class() {
        this(0.0f, 0.0f, 0.0f);
    }

    public FRotator_class(float pitch, float yaw, float roll) {
        this.Pitch = pitch;
        this.Yaw = yaw;
        this.Roll = roll;
    }

    public static float normalizeAxis(float angle) {
        // -180 ~ 180
        if (angle > 180.0f)
            angle -= 360.0f;
        if (angle < -180.0f)
            angle += 360.0f;
        return angle;
    }

    public void clamp() {
        Pitch = clampValue(normalizeAxis(Pitch), -75.0f, 75.0f);
        Yaw = normalizeAxis(Yaw);
        Roll = normalizeAxis(Roll);
    }

    private float clampValue(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public float innerProduct(FRotator_class v) {
        return (Pitch * v.Pitch) + (Yaw * v.Yaw) + (Roll * v.Roll);
    }

    public FRotator_class outerProduct(FRotator_class v) {
        return new FRotator_class(
                (Yaw * v.Roll) - (Roll * v.Yaw),
                (Roll * v.Pitch) - (Pitch * v.Roll),
                (Pitch * v.Yaw) - (Yaw * v.Pitch)
        );
    }

    public boolean equals(FRotator_class v) {
        return Pitch == v.Pitch && Yaw == v.Yaw && Roll == v.Roll;
    }

    public FRotator_class negate() {
        return new FRotator_class(-Pitch, -Yaw, -Roll);
    }

    public FRotator_class add(FRotator_class v) {
        return new FRotator_class(Pitch + v.Pitch, Yaw + v.Yaw, Roll + v.Roll);
    }

    public FRotator_class subtract(FRotator_class v) {
        return new FRotator_class(Pitch - v.Pitch, Yaw - v.Yaw, Roll - v.Roll);
    }

    public FRotator_class multiply(float value) {
        return new FRotator_class(Pitch * value, Yaw * value, Roll * value);
    }

    public float length() {
        return (float) Math.sqrt(Pitch * Pitch + Yaw * Yaw + Roll * Roll);
    }

    public float distance(FRotator_class v) {
        return subtract(v).length();
    }

    // 注意：以下方法需要其他类的实现
    // public FQuat getQuaternion() const;
    // public FRotator_class(FQuat q);
    // public FVector_class getUnitVector() const;
    // public FMatrix_class getMatrix(FVector_class origin) const;
}
