package tyut.selab.pubgmhd.domain;

/**
 * @ClassName: 对象信息
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 15:52
 * @Version: 1.0
 **/
public class 对象信息 {
    public int isCanRead;
    public int 敌人数量;
    public int 物品数量;
    public int 车辆数量;
    public 敌人信息 敌人信息;
    public 物品信息[] 物品信息 = new 物品信息[1000];
    public 车辆信息[] 车辆信息 = new 车辆信息[50];

    public 对象信息() {
        敌人信息 = new 敌人信息();

        // 初始化数组
        for (int i = 0; i < 物品信息.length; i++) {
            物品信息[i] = new 物品信息();
        }

        for (int i = 0; i < 车辆信息.length; i++) {
            车辆信息[i] = new 车辆信息();
        }
    }
}
