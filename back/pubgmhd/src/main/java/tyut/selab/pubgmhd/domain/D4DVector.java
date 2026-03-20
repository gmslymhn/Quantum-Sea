package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: D4DVector
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:06
 * @Version: 1.0
 **/
public class D4DVector {
    public float X;
    public float Y;
    public float Z;
    public float W;

    public D4DVector() {
        this(0, 0, 0, 0);
    }

    public D4DVector(float x, float y, float z, float w) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.W = w;
    }
}
