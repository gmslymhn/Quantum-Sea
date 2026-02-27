package tyut.selab.common.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: AccessLogAnnotation
 * @Description:访问日志注释,添加本注解，会记录本接口对应的访问记录
 * @Author: gmslymhn
 * @CreateTime: 2024-07-15 20:24
 * @Version: 1.0
 **/
@Target(ElementType.METHOD)//注解放置的目标位置即方法级别
@Retention(RetentionPolicy.RUNTIME)//注解在哪个阶段执行
@Documented
public @interface AccessLogAnnotation {
    String method() default "";
}
