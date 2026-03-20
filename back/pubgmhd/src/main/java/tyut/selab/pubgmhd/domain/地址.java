package tyut.selab.pubgmhd.domain;

import lombok.Data;

/**
 * @ClassName: 地址
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-16 14:29
 * @Version: 1.0
 **/

@Data
public class 地址 {

    public Long libue4 = 0L;
    public Long 世界地址 = 0L;
    public Long 类地址 = 0L;
    public Long 解密地址 = 0L;
    public Long 自身地址 = 0L;
    public Long 矩阵地址 = 0L;
    public Long 矩阵地址_Tol = 0L;
    public Long 数组地址 = 0L;
    public Long 相机 = 0L;

    public 地址(){
    }
    /**
     * 地址转换工具方法
     */
    public static String toHex(long value) {
        return "0x" + Long.toHexString(value);
    }
    public String toString(){
        return "地址：" +
                "\nlibue4="+toHex(libue4) +
                "\n世界地址="+toHex(世界地址) +
                "\n类地址="+toHex(类地址) +
                "\n解密地址="+toHex(解密地址) +
                "\n自身地址="+toHex(自身地址) +
                "\n矩阵地址="+toHex(矩阵地址) +
                "\n矩阵地址_Tol="+toHex(矩阵地址_Tol) +
                "\n数组地址="+toHex(数组地址) +
                "\n相机="+toHex(相机);

    }

}
