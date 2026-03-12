package tyut.selab;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import tyut.selab.Graduation.config.PostCrawler;
import tyut.selab.Graduation.domain.entity.PostDataEntity;
import tyut.selab.Graduation.mapper.PostDataMapper;

import java.util.List;


/**
 * @ClassName: Test
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2024-08-08 23:22
 * @Version: 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Test {




    @Autowired
    private PostCrawler postCrawler;
    @Autowired
    private PostDataMapper postDataMapper;

    @org.junit.Test
    public void test1(){

        List<PostDataEntity> postDataEntities = postCrawler.crawlPosts("1707546893");
        System.out.println(postDataEntities);
    }
    @org.junit.Test
    @Transactional
    @Rollback(false)
    public void testBatchCrawlWithTimestampLoop() {
        // 初始时间戳
        long startTimestamp = 1773132292;
        // 每次减少的时间
        long decrement = 300L;
        // 最大循环次数
        int maxLoops = 100;
        // 循环间隔（秒）
        int intervalSeconds = 10;

        log.info("开始批量爬取测试");
        log.info("起始时间戳: {}, 每次减: {}, 最大循环: {}次, 间隔: {}秒",
                startTimestamp, decrement, maxLoops, intervalSeconds);

        int totalSuccess = 0;
        int totalError = 0;
        int totalSkip = 0;

        long currentTimestamp = startTimestamp;

        for (int loopCount = 1; loopCount <= maxLoops; loopCount++) {
            log.info("第 {} 次循环开始，当前时间戳: {}", loopCount, currentTimestamp);

            try {
                // 1. 使用当前时间戳作为参数爬取数据
                List<PostDataEntity> postDataEntities = postCrawler.crawlPosts(String.valueOf(currentTimestamp));

                if (postDataEntities == null || postDataEntities.isEmpty()) {
                    log.warn("第 {} 次循环未获取到帖子数据，时间戳: {}", loopCount, currentTimestamp);
                } else {
                    log.info("第 {} 次循环获取到 {} 条帖子数据", loopCount, postDataEntities.size());

                    int loopSuccess = 0;
                    int loopError = 0;
                    int loopSkip = 0;

                    // 2. 处理每条数据
                    for (PostDataEntity post : postDataEntities) {
                        try {
                            if (post.getThreadId() == null) {
                                log.warn("thread_id为空，跳过处理");
                                loopSkip++;
                                continue;
                            }

                            // 查询是否已存在
                            LambdaQueryWrapper<PostDataEntity> queryWrapper = new LambdaQueryWrapper<>();
                            queryWrapper.eq(PostDataEntity::getThreadId, post.getThreadId());

                            PostDataEntity existingPost = postDataMapper.selectOne(queryWrapper);

                            if (existingPost == null) {
                                try {
                                    // 尝试插入
                                    int result = postDataMapper.insert(post);
                                    if (result > 0) {
                                        loopSuccess++;
                                        log.debug("插入成功: thread_id={}", post.getThreadId());
                                    } else {
                                        loopError++;
                                        log.error("插入失败: thread_id={}", post.getThreadId());
                                    }
                                } catch (Exception e) {
                                    // 捕获唯一约束异常
                                    if (e.getMessage().contains("Duplicate entry")) {
                                        loopSkip++;
                                        log.debug("数据已存在，跳过: thread_id={}", post.getThreadId());
                                    } else {
                                        loopError++;
                                        log.error("插入异常: thread_id={}, error={}",
                                                post.getThreadId(), e.getMessage());
                                    }
                                }
                            } else {
                                // 已存在，执行更新
                                post.setPostId(existingPost.getPostId());
                                int result = postDataMapper.updateById(post);
                                if (result > 0) {
                                    loopSuccess++;
                                    log.debug("更新成功: thread_id={}", post.getThreadId());
                                } else {
                                    loopError++;
                                    log.error("更新失败: thread_id={}", post.getThreadId());
                                }
                            }
                        } catch (Exception e) {
                            loopError++;
                            log.error("处理数据异常: thread_id={}, error={}",
                                    post.getThreadId(), e.getMessage());
                        }
                    }

                    // 3. 记录本次循环结果
                    log.info("第 {} 次循环完成: 成功 {} 条, 失败 {} 条, 跳过 {} 条",
                            loopCount, loopSuccess, loopError, loopSkip);

                    totalSuccess += loopSuccess;
                    totalError += loopError;
                    totalSkip += loopSkip;
                }

            } catch (Exception e) {
                log.error("第 {} 次循环执行失败，时间戳: {}, 错误: {}",
                        loopCount, currentTimestamp, e.getMessage());
                totalError++;
            }

            // 4. 更新下一个时间戳
            currentTimestamp -= decrement;

            // 5. 如果不是最后一次循环，等待指定间隔
            if (loopCount < maxLoops) {
                try {
                    log.info("等待 {} 秒后进行下一次循环...", intervalSeconds);
                    Thread.sleep(intervalSeconds * 1000L);
                } catch (InterruptedException e) {
                    log.error("等待被中断: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // 6. 输出最终结果
        log.info("批量爬取测试执行完成");
        log.info("总计: 成功 {} 条, 失败 {} 条, 跳过 {} 条",
                totalSuccess, totalError, totalSkip);

        System.out.println("========================================");
        System.out.println("批量爬取测试结果:");
        System.out.println("起始时间戳: " + startTimestamp);
        System.out.println("每次减少: " + decrement);
        System.out.println("循环次数: " + maxLoops);
        System.out.println("间隔时间: " + intervalSeconds + "秒");
        System.out.println("总计成功: " + totalSuccess + " 条");
        System.out.println("总计失败: " + totalError + " 条");
        System.out.println("总计跳过: " + totalSkip + " 条");
        System.out.println("========================================");
    }

}
