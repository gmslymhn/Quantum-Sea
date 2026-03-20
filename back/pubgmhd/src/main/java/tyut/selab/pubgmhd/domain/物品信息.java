package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: 物品信息
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:52
 * @Version: 1.0
 **/
public class 物品信息 {
    public D3DVector 坐标;
    public int 物品;
    public int 距离;

    public 物品信息() {
        坐标 = new D3DVector();
    }
}
