package tyut.selab;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



/**
 * @ClassName: Test
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2024-08-08 23:22
 * @Version: 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {



    @org.junit.Test
    public void test3(){
    }
    /**
     * 测试方法
     */
    public static void main(String[] args) {
        for (int i=1;i<1000;i++){
            double r1 = (double) i /1000;
            double r2 = (double) i /(i+1000);
            System.out.println("增长率："+r1 +"   偏移增长率："+r2+"   差值："+(r1-r2) );
        }
    }

}
