package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: D3DVector
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:06
 * @Version: 1.0
 **/
public class D3DVector {
    public float X;
    public float Y;
    public float Z;

    public D3DVector() {
        this(0, 0, 0);
    }

    public D3DVector(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
}
