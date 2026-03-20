package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: 敌人信息
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:51
 * @Version: 1.0
 **/
public class 敌人信息 {
    public FVector_class 坐标;
    public FVector_class 相机坐标;
    public FRotator_class 准星;
    public D3DVector 向量;
    public FVector_class[] 骨骼坐标 = new FVector_class[17];
    public D2DVector 雷达;
    public D2DVector 对象骨骼;
    public boolean 烟雾中 = true;
    public int isboot;
    public int 队伍;
    public int 状态;
    public int 距离;
    public int 手持;
    public float 当前血量;
    public float 最大血量;
    public boolean 乘坐载具;
    public String 名字;
    public float Rotator;
    public float 头;
    public float 甲;
    public long 实体地址;
    public long 角色实体;
    public long 实体列表地址;
    public int 实体数量;
    public boolean isView = true;
    public int 头甲包id;
    public long 头甲包地址;
    public int 子弹数量;
    public int 子弹最大数量;
    public int 高级人机;

    @Override
    public String toString() {
        return "敌人信息{" +
                "坐标=" + 坐标 +
                ", 相机坐标=" + 相机坐标 +
                ", isboot=" + isboot +
                ", 队伍=" + 队伍 +
                ", 状态=" + 状态 +
                ", 当前血量=" + 当前血量 +
                ", 最大血量=" + 最大血量 +
                ", 名字='" + 名字 + '\'' +
                ", 高级人机=" + 高级人机 +
                '}';
    }

    // 构造函数
    public 敌人信息() {
        // 初始化数组
        for (int i = 0; i < 骨骼坐标.length; i++) {
            骨骼坐标[i] = new FVector_class();
        }
        坐标 = new FVector_class();
        相机坐标 = new FVector_class();
        准星 = new FRotator_class();
        向量 = new D3DVector();
        雷达 = new D2DVector();
        对象骨骼 = new D2DVector();
    }
}
