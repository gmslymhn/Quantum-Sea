package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: D2DVector
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:06
 * @Version: 1.0
 **/
public class D2DVector {
    public float X;
    public float Y;

    public D2DVector() {
        this(0, 0);
    }

    public D2DVector(float x, float y) {
        this.X = x;
        this.Y = y;
    }
}
