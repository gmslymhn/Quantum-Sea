package tyut.selab.pubgmhd;

import tyut.selab.pubgmhd.domain.*;
import tyut.selab.pubgmhd.utils.读写;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: main
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 14:31
 * @Version: 1.0
 **/
public class main {

    /**
     * 地址转换工具方法
     */
    public static String toHex(long value) {
        return "0x" + Long.toHexString(value);
    }

    public static String toHex(int value) {
        return "0x" + Integer.toHexString(value);
    }

    public static long fromHex(String hex) {
        return Long.parseLong(hex.replace("0x", ""), 16);
    }

    public static void main(String[] args) {
        读写 读写 = new 读写("192.168.137.246");
        地址 地址 = new 地址();
        自身数据 自身数据 = new 自身数据();
        对象地址 对象地址 = new 对象地址();
        对象信息 对象信息 = new 对象信息();
        敌人信息 敌人信息 = new 敌人信息();

        int 绘制人机 = 0;
        int 绘制真人 = 0;
        int 世界数量 = 0;
        Long 解密数组 = 0L;
        Long baseAddress = 读写.getModuleBase("libUE4.so");
        System.out.println("模块基址: " + toHex(baseAddress));
        地址.libue4 = baseAddress;
        地址.世界地址 = 读写.getPtr64(读写.getPtr64(地址.libue4 + 0x14988578)+ 0xb0);
        地址.自身地址 = 读写.getPtr64(读写.getPtr64(读写.getPtr64(读写.getPtr64(读写.getPtr64(地址.libue4 + 0x14988578) + 0xb8) + 0x88) + 0x30) + 0x3438); // 自身
        地址.矩阵地址 = 读写.getPtr64(读写.getPtr64(地址.libue4 + 0x14954368) + 0x20) + 0x270;                                                             // 矩阵maritx
        地址.矩阵地址_Tol = 读写.getPtr64(读写.getPtr64(地址.libue4 + 0x1475B6B8) + 0x98) + 0x10440;
        地址.类地址 = 读写.getPtr64(地址.libue4 + 0x141410A8);
        地址.数组地址 = 读写.getPtr64(地址.世界地址+0xA0);
//        地址.数组地址 = 读写.getPtr64(读写.getPtr64(读写.getPtr64(读写.getPtr64(地址.libue4 + 0x141BF3F8) + 0xf8) + 0x138) + 0xf0);
        世界数量 = 读写.getDword(地址.世界地址 + 0xA8);
        System.out.println(地址);
        System.out.println("世界数量:"+世界数量);
        if (解密数组 == 1)
        {
            地址.数组地址 = 解密数组;
            世界数量 = Math.toIntExact(读写.getPtr64(读写.getPtr64(读写.getPtr64(读写.getPtr64(地址.libue4 + 0x14214140) + 0xF8) + 0x2D8) + 0xF0));
        }

        自身数据.自身队伍 = 读写.getDword(地址.自身地址 + 0xb68);
        自身数据.自身状态 = 读写.getDword(读写.getPtr64(地址.自身地址 + 0x1660));
        自身数据.手持id = 读写.getDword(读写.getPtr64(地址.自身地址 + 0x10e8) + 0xcf8);
        自身数据.手持 = 自身数据.手持id;
        自身数据.矩阵 = get矩阵(地址.矩阵地址);

        自身数据.NowRot = getRotator(地址.自身地址 + 0x5b28 + 0x5f8);
        long Controller = 读写.getPtr64(地址.自身地址 + 0x5b28);
        long CamManager = 读写.getPtr64(Controller + 0x658);
        if (CamManager != 0)
        {
// 读取相机坐标
            自身数据.相机坐标 = getFVector_class(CamManager + 0x650);
// 读取准星

            自身数据.准星 = getFRotator_class(CamManager + 0x650 + 0x18);
            自身数据.Fov = 读写.getFloat(CamManager + 0x680);
            自身数据.准星Y = 读写.getFloat(Controller + 0x5fc) - 90;// 读取坐标（与相机坐标相同地址）

            自身数据.坐标 = getFVector_class(CamManager + 0x650);
        }
        自身数据.人物高度 = 读写.getFloat(地址.自身地址 + 0x1038);

        System.out.println(自身数据);

        for (int a = 0; a < 世界数量; a++) {
            对象地址.敌人地址 = 读写.getPtr64(地址.数组地址 + a * 8);
//            if (0 == 对象地址.敌人地址){
//                System.out.println("地址未空");
//                continue;
//            }
            对象信息.敌人信息.坐标 = getFVector_class(读写.getPtr64(对象地址.敌人地址 + 0x268) + 0x200);

            String ClassName = "";
            Long ClassID =读写.getPtr64(对象地址.敌人地址 + 24);
            long FNameEntry;
            FNameEntry = 读写.getPtr64(读写.getPtr64(地址.类地址 + (ClassID / 0x4000) * 0x8) + (ClassID % 0x4000) * 0x8);

            byte[] data = 读写.readMemory(FNameEntry + 0xC, 64);
            if (data != null) {
                // 找到第一个null终止符的位置
                int length = 0;
                while (length < data.length && data[length] != 0) {
                    length++;
                }
                // 使用正确的字符集（通常是UTF-8或ASCII）
                ClassName = new String(data, 0, length, StandardCharsets.UTF_8);
            }
            System.out.println("ClassName:"+ClassName);
            System.out.println("ClassID:"+ClassID);


// 检查是否是特定类型的敌人
            if (ClassName.contains("BPPawn_Escape_Raven") || ClassName.contains("BPPawn_Escape_UAV_C")) {
                continue;
            }

// 检查是否是特殊敌人
            if (读写.getFloat(对象地址.敌人地址 + 0x37e8) == 479.5f ||
                    ClassName.contains("BPPawn_Escape_") ) {
                对象信息.敌人信息.队伍 = 读写.getDword(对象地址.敌人地址 + 0xb68);
                对象信息.敌人信息.isboot = (对象信息.敌人信息.队伍 == -1) ? 1 : 读写.getDword(对象地址.敌人地址 + 0xb84);
                对象信息.敌人信息.高级人机 = 读写.getDword(对象地址.敌人地址 + 0xb88);
                对象信息.敌人信息.名字 = 读写.getUtf8(读写.getPtr64(对象地址.敌人地址 + 0xae8));

                if (对象信息.敌人信息.isboot == 1) {
                    continue;
                }
                对象信息.敌人信息.状态 = 读写.getDword(读写.getPtr64(对象地址.敌人地址 + 0x1660)); // 敌人状态
//                对象信息.敌人信息.雷达 = 计算.rotateCoord(自身数据.准星Y, (自身数据.坐标.X - 对象信息.敌人信息.坐标.X) / 200, (自身数据.坐标.Y - 对象信息.敌人信息.坐标.Y) / 200);
//                读写.readv(对象地址.敌人地址 + VelocitySafety, &对象信息.敌人信息.向量, sizeof(对象信息.敌人信息.向量)); // 敌人向量
                对象信息.敌人信息.Rotator = 读写.getFloat(对象地址.敌人地址 + 0x1A8);    //用于被瞄准
                对象信息.敌人信息.当前血量 = 读写.getFloat(对象地址.敌人地址 + 0xfd8);      // 血量
                对象信息.敌人信息.最大血量 = 读写.getFloat(对象地址.敌人地址 + 0xfe0);   // 最大血量
                对象信息.敌人信息.角色实体 = 读写.getPtr64(对象地址.敌人地址 + 0x39b0);
                对象信息.敌人信息.实体列表地址 = 读写.getPtr64(对象信息.敌人信息.角色实体 + 0x818) + 0x8;
                对象信息.敌人信息.实体数量 = 读写.getDword(对象信息.敌人信息.角色实体 + 0x818 + 0x8);
                long  MeshOffset = 读写.getPtr64(对象地址.敌人地址 + 0x650); // mesh
                long Bonecount = 读写.getPtr64(MeshOffset + 0x810 + 8);      // 骨骼节点

//                对象信息.敌人信息.名字 = 读写.getUtf8(读写.getPtr64(对象地址.敌人地址 + 0xae8)); // 敌人名字

                if (ClassName.equals("BPPawn_Escape_Raven")|| ClassName.equals("BPPawn_Escape_UAV_C"))
                {
                    continue;
                }
                if (对象信息.敌人信息.状态 == 1048592 || 对象信息.敌人信息.状态 == 1048576)
                    continue;
                if (对象信息.敌人信息.isboot == 1)
                {
                    绘制人机++;
                }
                else
                {
                    绘制真人++;
                }
                System.out.println(对象信息.敌人信息);
            }
        }

    }

    public static 自身数据.Rotator getRotator(long address){
        byte[] data = 读写.readMemory(address, 12); // Rotator有3个float，每个4字节，共12字节
        if (null == data){
            return null;
        }
            // 将字节数据转换为Rotator对象
            ByteBuffer buffer = ByteBuffer.wrap(data);
            buffer.order(ByteOrder.LITTLE_ENDIAN); // 根据C++内存布局设置字节序

            float pitch = buffer.getFloat();
            float yaw = buffer.getFloat();
            float roll = buffer.getFloat();

            return new 自身数据.Rotator(pitch, yaw, roll);
    }
    public static FVector_class getFVector_class(long address){
        byte[] data = 读写.readMemory(address, 12); // FVector_class 3个float
        if (null == data){
            return null;
        }
            ByteBuffer buffer = ByteBuffer.wrap(data);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            return new FVector_class(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
    }

    public static float[] get矩阵(long address){
        byte[] data = 读写.readMemory(address, 64); // FVector_class 16个float
        if (null == data){
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        float[] 矩阵 = new float[16];
        // 读取16个float值到矩阵
        for (int i = 0; i < 16; i++) {
            矩阵[i] = buffer.getFloat();
        }
        return 矩阵;
    }
    public static FRotator_class getFRotator_class(long address){
        byte[] data = 读写.readMemory(address, 12); // FRotator_class 3个float
        if (null == data){
            return null;
        }
            ByteBuffer buffer = ByteBuffer.wrap(data);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            return new FRotator_class(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
    }

}
