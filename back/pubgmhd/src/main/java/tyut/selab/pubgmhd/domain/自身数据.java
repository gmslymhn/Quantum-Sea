package tyut.selab.pubgmhd.domain;

import lombok.Data;

@Data
public class 自身数据 {
    public FVector_class 坐标;
    public FVector_class 相机坐标;
    public FRotator_class 准星;
    public D3DVector 视角;
    public float[] 矩阵 = new float[16];
    public int 自身队伍;
    public int 自身状态;
    public int 真人数量;
    public int 总人数;
    public int 人机数量;
    public int 队伍数量;
    public int 手持;
    public int 手持id;
    public int 手持握把;
    public float Fov; // 视场角
    public float 准星Y;
    public float 人物高度;
    public D2DVector 自身角度;
    public Rotator NowRot; // 当前旋转
    public D3DVector CameraLocation; // 相机位置
    public D3DVector Firearms; // 武器位置

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("自身数据：\n");
        sb.append("坐标：").append(坐标 != null ? 坐标.toString() : "null").append("\n");
        sb.append("相机坐标：").append(相机坐标 != null ? 相机坐标.toString() : "null").append("\n");
        sb.append("准星：").append(准星 != null ? 准星.toString() : "null").append("\n");
        sb.append("视角：").append(视角 != null ? 视角.toString() : "null").append("\n");
        sb.append("矩阵：").append(java.util.Arrays.toString(矩阵)).append("\n");
        sb.append("自身队伍：").append(自身队伍).append("\n");
        sb.append("自身状态：").append(自身状态).append("\n");
        sb.append("真人数量：").append(真人数量).append("\n");
        sb.append("总人数：").append(总人数).append("\n");
        sb.append("人机数量：").append(人机数量).append("\n");
        sb.append("队伍数量：").append(队伍数量).append("\n");
        sb.append("手持：").append(手持).append("\n");
        sb.append("手持id：").append(手持id).append("\n");
        sb.append("手持握把：").append(手持握把).append("\n");
        sb.append("Fov：").append(Fov).append("\n");
        sb.append("准星Y：").append(准星Y).append("\n");
        sb.append("人物高度：").append(人物高度).append("\n");
        sb.append("自身角度：").append(自身角度 != null ? 自身角度.toString() : "null").append("\n");
        sb.append("NowRot：").append(NowRot != null ? NowRot.toString() : "null").append("\n");
        sb.append("CameraLocation：").append(CameraLocation != null ? CameraLocation.toString() : "null").append("\n");
        sb.append("Firearms：").append(Firearms != null ? Firearms.toString() : "null").append("\n");
        return sb.toString();
    }


    //本项目仅用于学习和研究，不用于任何商业用途 否则自己承担所有风险
    public static class Rotator {
        public float Pitch;
        public float Yaw;
        public float Roll;

        public Rotator() {}
        @Override
        public String toString() {
            return String.format("(pitch=%f, yaw=%f, roll=%f)", Pitch, Yaw, Roll);
        }

        public Rotator(float pitch, float yaw, float roll) {
            this.Pitch = pitch;
            this.Yaw = yaw;
            this.Roll = roll;
        }
    }
}
