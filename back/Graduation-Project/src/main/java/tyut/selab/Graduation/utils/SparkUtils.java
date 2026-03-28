//package tyut.selab.Graduation.utils;
//
//import org.apache.spark.sql.SparkSession;
//
///**
// * @ClassName: SparkUtils
// * @Description:
// * @Author: gmslymhn
// * @CreateTime: 2026-03-26 13:57
// * @Version: 1.0
// **/
//public class SparkUtils {
//    private static SparkSession sparkSession = null;
//
//    public static synchronized SparkSession getSparkSession() {
//        if (sparkSession == null) {
//            // 在静态初始化块中设置系统属性
//            System.setProperty("org.apache.spark.log.level", "OFF");
//            System.setProperty("spark.driver.log.disable", "true");
//
//            sparkSession = SparkSession.builder()
//                    .appName("SpringBoot-Spark")
//                    .master("local[*]")
//                    .config("spark.driver.memory", "2g")
//                    .config("spark.executor.memory", "4g")
//                    .config("spark.ui.enabled", "false")
//                    .config("spark.eventLog.enabled", "false")
//                    .getOrCreate();
//        }
//        return sparkSession;
//    }
//
//    public static void stopSpark() {
//        if (sparkSession != null) {
//            sparkSession.stop();
//            sparkSession = null;
//        }
//    }
//}