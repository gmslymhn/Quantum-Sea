package tyut.selab.Graduation.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tyut.selab.Graduation.domain.entity.PostDataEntity;
import tyut.selab.Graduation.domain.entity.LizardLogEntity;
import tyut.selab.Graduation.mapper.PostDataMapper;
import tyut.selab.Graduation.mapper.LizardLogMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: ScheduledSyncService
 * @Description: 注解单线程定时任务
 * @Author: gmslymhn
 * @CreateTime: 2026-03-09 21:43
 * @Version: 1.0
 **/
@Slf4j
@Component
public class ScheduledSyncService {

    @Autowired
    private PostDataMapper postDataMapper;

    @Autowired
    private PostCrawler postCrawler;

    @Autowired
    private LizardLogMapper lizardLogMapper;

    /**
     * 定时爬取任务：每60秒执行一次
     * 爬取太原理工大学集市最新消息，并同步到数据库
     */
    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void executeScheduledTask() {
        long startTime = System.currentTimeMillis();
        log.info("定时爬取任务：开始爬取太原理工大学集市最新消息");
        log.info("爬取开始时间：{}", startTime);

        try {
            // 1. 从爬虫服务获取最新的帖子列表
            List<PostDataEntity> postDataList = postCrawler.crawlPosts();

            if (postDataList == null || postDataList.isEmpty()) {
                log.warn("本次爬取未获取到帖子数据");
                // 记录爬取日志（即使没有数据也要记录）
                saveCrawlLog("爬取错误！");
                return;
            }

            log.info("本次爬取获取到 {} 条帖子数据", postDataList.size());

            int insertCount = 0;
            int updateCount = 0;
            int errorCount = 0;

            // 2. 遍历帖子列表，保存或更新到数据库
            for (PostDataEntity post : postDataList) {
                try {
                    // 根据thread_id查询是否已存在
                    LambdaQueryWrapper<PostDataEntity> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(PostDataEntity::getThreadId, post.getThreadId());

                    PostDataEntity existingPost = postDataMapper.selectOne(queryWrapper);

                    if (existingPost == null) {
                        // 不存在，执行插入操作
                        int result = postDataMapper.insert(post);
                        if (result > 0) {
                            insertCount++;
                            log.debug("插入新帖子成功: thread_id={}, title={}",
                                    post.getThreadId(), post.getTitle());
                        } else {
                            errorCount++;
                            log.error("插入新帖子失败: thread_id={}", post.getThreadId());
                        }
                    } else {
                        // 已存在，执行更新操作
                        // 设置要更新的字段（排除主键和创建时间）
                        post.setPostId(existingPost.getPostId());

                        LambdaUpdateWrapper<PostDataEntity> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(PostDataEntity::getThreadId, post.getThreadId());

                        int result = postDataMapper.update(post, updateWrapper);
                        if (result > 0) {
                            updateCount++;
                            log.debug("更新帖子成功: thread_id={}, title={}",
                                    post.getThreadId(), post.getTitle());
                        } else {
                            errorCount++;
                            log.error("更新帖子失败: thread_id={}", post.getThreadId());
                        }
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.error("处理帖子数据时发生异常, thread_id={}: {}",
                            post.getThreadId(), e.getMessage(), e);
                }
            }

            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;

            // 3. 记录执行结果
            log.info("定时爬取任务执行完成，耗时: {}ms", costTime);
            log.info("处理结果: 新增 {} 条, 更新 {} 条, 失败 {} 条",
                    insertCount, updateCount, errorCount);

            // 4. 保存爬取日志
            saveCrawlLog(postDataList.toString());

        } catch (Exception e) {
            log.error("定时爬取任务执行失败: ", e);
            // 即使任务失败也记录日志
            saveCrawlLog("爬取失败!"); // 使用-1表示爬取失败
        }
    }

    /**
     * 保存爬取日志到数据库
     * @param crawledCount 爬取到的数据条数
     */
    private void saveCrawlLog(String crawledCount) {
        try {
            LizardLogEntity logEntity = new LizardLogEntity();
            logEntity.setLizardData(crawledCount);
            // lizardTime字段由数据库自动填充，这里不需要设置

            int result = lizardLogMapper.insert(logEntity);
            if (result > 0) {
                log.debug("爬取日志保存成功: crawledCount={}", crawledCount);
            } else {
                log.error("爬取日志保存失败: crawledCount={}", crawledCount);
            }
        } catch (Exception e) {
            log.error("保存爬取日志时发生异常: ", e);
        }
    }
}
