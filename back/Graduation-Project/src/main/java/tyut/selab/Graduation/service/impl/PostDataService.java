package tyut.selab.Graduation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tyut.selab.Graduation.config.PostCrawler;
import tyut.selab.Graduation.domain.PostParam;
import tyut.selab.Graduation.domain.TimeRangeParam;
import tyut.selab.Graduation.domain.entity.LizardLogEntity;
import tyut.selab.Graduation.domain.entity.PostDataEntity;
import tyut.selab.Graduation.mapper.LizardLogMapper;
import tyut.selab.Graduation.mapper.PostDataMapper;
import tyut.selab.Graduation.service.IPostDataService;
import tyut.selab.common.domain.R;
import tyut.selab.common.utils.StringUtils;
import tyut.selab.framework.domain.PageParam;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PostDataService
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-25 16:09
 * @Version: 1.0
 **/
@Service
@Slf4j
public class PostDataService implements IPostDataService {

@Autowired
private PostDataMapper postDataMapper;
@Autowired
private LizardLogMapper lizardLogMapper;

@Autowired
private PostCrawler postCrawler;

    @Override
    public R getPostDataList(PostParam postParam) {
        Page<PostDataEntity> page = new Page<>(postParam.getPageNum(), postParam.getPageSize());
        QueryWrapper<PostDataEntity> queryWrapper = new QueryWrapper<>();

        // 搜索内容条件（标题或内容包含搜索词）
        if (StringUtils.isNotEmpty(postParam.getSearchContent())) {
            queryWrapper.and(wrapper -> wrapper
                    .like("title", postParam.getSearchContent())
                    .or()
                    .like("content", postParam.getSearchContent())
            );
        }

        // 分类名称条件（完全匹配）
        if (StringUtils.isNotEmpty(postParam.getCateName())) {
            queryWrapper.eq("cate_name", postParam.getCateName());
        }

        // 时间戳条件（按日期搜索）
        if (postParam.getPTime() != null) {
            // 将时间戳转换为当天的开始和结束时间戳
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(postParam.getPTime()),
                    ZoneId.systemDefault()
            );
            LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

            long startTimestamp = startOfDay.atZone(ZoneId.systemDefault()).toEpochSecond();
            long endTimestamp = endOfDay.atZone(ZoneId.systemDefault()).toEpochSecond();

            queryWrapper.between("p_time", startTimestamp, endTimestamp);
        }

        // 按发布时间倒序排序（最新在前）
        queryWrapper.orderByDesc("p_time");

        IPage<PostDataEntity> postDataEntityIPage = postDataMapper.selectPage(page, queryWrapper);
        return R.success(postDataEntityIPage);
    }

    @Override
    public R getLizardLogList(PageParam pageParam) {
        Page<LizardLogEntity> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());
        QueryWrapper<LizardLogEntity> queryWrapper = new QueryWrapper<>();

        // 按照创建时间从最新到最旧排序
        queryWrapper.orderByDesc("lizard_time");

        IPage<LizardLogEntity> lizardLogEntityIPage = lizardLogMapper.selectPage(page, queryWrapper);
        return R.success(lizardLogEntityIPage);
    }

    @Override
    public R getNewPostData(){
        List<PostDataEntity> postDataList = postCrawler.crawlPosts();
        if (postDataList == null || postDataList.isEmpty()) {
            return R.error("爬取失败！");
        }
        return R.success(postDataList);
    }
    @Override
    public R getSummaryData() {
        try {
            // 创建汇总数据对象
            Map<String, Object> summaryData = new HashMap<>();

            // 1. 总帖子数（MyBatis-Plus会自动处理逻辑删除）
            Long totalPosts = postDataMapper.selectCount(null);
            summaryData.put("totalPosts", totalPosts);

            // 2. 总浏览数
            QueryWrapper<PostDataEntity> viewWrapper = new QueryWrapper<>();
            viewWrapper.select("SUM(view_count) as totalViews");
            Map<String, Object> viewMap = postDataMapper.selectMaps(viewWrapper).stream().findFirst().orElse(new HashMap<>());
            Long totalViews = ((Number) viewMap.getOrDefault("totalViews", 0)).longValue();
            summaryData.put("totalViews", totalViews);

            // 3. 总评论数
            QueryWrapper<PostDataEntity> commentWrapper = new QueryWrapper<>();
            commentWrapper.select("SUM(c_count) as totalComments");
            Map<String, Object> commentMap = postDataMapper.selectMaps(commentWrapper).stream().findFirst().orElse(new HashMap<>());
            Long totalComments = ((Number) commentMap.getOrDefault("totalComments", 0)).longValue();
            summaryData.put("totalComments", totalComments);

            // 4. 总点赞数
            QueryWrapper<PostDataEntity> likeWrapper = new QueryWrapper<>();
            likeWrapper.select("SUM(l_count) as totalLikes");
            Map<String, Object> likeMap = postDataMapper.selectMaps(likeWrapper).stream().findFirst().orElse(new HashMap<>());
            Long totalLikes = ((Number) likeMap.getOrDefault("totalLikes", 0)).longValue();
            summaryData.put("totalLikes", totalLikes);

            // 5. 平均浏览数
            Double avgViews = totalPosts > 0 ? totalViews.doubleValue() / totalPosts : 0.0;
            summaryData.put("avgViews", String.format("%.2f", avgViews));

            // 6. 平均评论数
            Double avgComments = totalPosts > 0 ? totalComments.doubleValue() / totalPosts : 0.0;
            summaryData.put("avgComments", String.format("%.2f", avgComments));

            // 7. 平均点赞数
            Double avgLikes = totalPosts > 0 ? totalLikes.doubleValue() / totalPosts : 0.0;
            summaryData.put("avgLikes", String.format("%.2f", avgLikes));

            // 8. 今日新增帖子数
            QueryWrapper<PostDataEntity> todayWrapper = new QueryWrapper<>();
            todayWrapper.apply("DATE(create_time) = CURDATE()");
            Long todayPosts = postDataMapper.selectCount(todayWrapper);
            summaryData.put("todayPosts", todayPosts);

            // 9. 昨日新增帖子数
            QueryWrapper<PostDataEntity> yesterdayWrapper = new QueryWrapper<>();
            yesterdayWrapper.apply("DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)");
            Long yesterdayPosts = postDataMapper.selectCount(yesterdayWrapper);
            summaryData.put("yesterdayPosts", yesterdayPosts);

            // 10. 按分类统计帖子数
            QueryWrapper<PostDataEntity> categoryWrapper = new QueryWrapper<>();
            categoryWrapper.select("cate_name, COUNT(*) as count")
                    .groupBy("cate_name")
                    .orderByDesc("count");
            List<Map<String, Object>> categoryStats = postDataMapper.selectMaps(categoryWrapper);
            summaryData.put("categoryStats", categoryStats);

            // 11. 热门帖子（浏览数前10）
            QueryWrapper<PostDataEntity> hotPostsWrapper = new QueryWrapper<>();
            hotPostsWrapper.select("thread_id, title, view_count, c_count, l_count, cate_name, nickname")
                    .orderByDesc("view_count")
                    .last("LIMIT 10");
            List<Map<String, Object>> hotPosts = postDataMapper.selectMaps(hotPostsWrapper);
            summaryData.put("hotPosts", hotPosts);

            // 12. 活跃用户（发帖数前10）
            QueryWrapper<PostDataEntity> activeUsersWrapper = new QueryWrapper<>();
            activeUsersWrapper.select("nickname, headimgurl, COUNT(*) as post_count, SUM(view_count) as total_views")
                    .groupBy("nickname, headimgurl")
                    .orderByDesc("post_count")
                    .last("LIMIT 10");
            List<Map<String, Object>> activeUsers = postDataMapper.selectMaps(activeUsersWrapper);
            summaryData.put("activeUsers", activeUsers);

            // 13. 最近7天发帖趋势
            QueryWrapper<PostDataEntity> trendWrapper = new QueryWrapper<>();
            trendWrapper.select("DATE(create_time) as date, COUNT(*) as count")
                    .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)")
                    .groupBy("DATE(create_time)")
                    .orderByAsc("DATE(create_time)");
            List<Map<String, Object>> postTrend = postDataMapper.selectMaps(trendWrapper);
            summaryData.put("postTrend", postTrend);

            // 14. 用户等级分布
            QueryWrapper<PostDataEntity> levelWrapper = new QueryWrapper<>();
            levelWrapper.select("user_level_title, COUNT(*) as count")
                    .groupBy("user_level_title")
                    .orderByDesc("count");
            List<Map<String, Object>> levelDistribution = postDataMapper.selectMaps(levelWrapper);
            summaryData.put("levelDistribution", levelDistribution);

            // 15. 帖子状态统计
            QueryWrapper<PostDataEntity> statusWrapper = new QueryWrapper<>();
            statusWrapper.select("finish_status, COUNT(*) as count")
                    .groupBy("finish_status");
            List<Map<String, Object>> statusStats = postDataMapper.selectMaps(statusWrapper);
            summaryData.put("statusStats", statusStats);

            return R.success(summaryData);
        } catch (Exception e) {
            return R.error("获取汇总数据失败");
        }
    }

    @Override
    public R getPostTrendAnalysis(TimeRangeParam param) {
        try {
            Map<String, Object> analysisData = new HashMap<>();

            // 默认最近30天
            Integer days = param.getDays() != null ? param.getDays() : 30;

            // 发帖趋势
            QueryWrapper<PostDataEntity> trendWrapper = new QueryWrapper<>();
            trendWrapper.select("DATE(create_time) as date, COUNT(*) as count")
                    .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL " + days + " DAY)")
                    .groupBy("DATE(create_time)")
                    .orderByAsc("DATE(create_time)");
            List<Map<String, Object>> postTrend = postDataMapper.selectMaps(trendWrapper);
            analysisData.put("postTrend", postTrend);

            // 浏览趋势
            QueryWrapper<PostDataEntity> viewTrendWrapper = new QueryWrapper<>();
            viewTrendWrapper.select("DATE(create_time) as date, SUM(view_count) as total_views")
                    .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL " + days + " DAY)")
                    .groupBy("DATE(create_time)")
                    .orderByAsc("DATE(create_time)");
            List<Map<String, Object>> viewTrend = postDataMapper.selectMaps(viewTrendWrapper);
            analysisData.put("viewTrend", viewTrend);

            // 评论趋势
            QueryWrapper<PostDataEntity> commentTrendWrapper = new QueryWrapper<>();
            commentTrendWrapper.select("DATE(create_time) as date, SUM(c_count) as total_comments")
                    .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL " + days + " DAY)")
                    .groupBy("DATE(create_time)")
                    .orderByAsc("DATE(create_time)");
            List<Map<String, Object>> commentTrend = postDataMapper.selectMaps(commentTrendWrapper);
            analysisData.put("commentTrend", commentTrend);

            // 点赞趋势
            QueryWrapper<PostDataEntity> likeTrendWrapper = new QueryWrapper<>();
            likeTrendWrapper.select("DATE(create_time) as date, SUM(l_count) as total_likes")
                    .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL " + days + " DAY)")
                    .groupBy("DATE(create_time)")
                    .orderByAsc("DATE(create_time)");
            List<Map<String, Object>> likeTrend = postDataMapper.selectMaps(likeTrendWrapper);
            analysisData.put("likeTrend", likeTrend);

            // 计算增长率
            if (postTrend.size() >= 2) {
                long currentCount = ((Number) postTrend.get(postTrend.size() - 1).get("count")).longValue();
                long previousCount = ((Number) postTrend.get(postTrend.size() - 2).get("count")).longValue();
                double growthRate = previousCount > 0 ?
                        ((currentCount - previousCount) * 100.0 / previousCount) : 0;
                analysisData.put("growthRate", String.format("%.2f", growthRate));
            }

            return R.success(analysisData);
        } catch (Exception e) {
            return R.error("获取趋势分析数据失败");
        }
    }

    @Override
    public R getUserBehaviorAnalysis() {
        try {
            Map<String, Object> analysisData = new HashMap<>();

            // 1. 用户发帖分布 - 使用原生SQL查询
            String postDistributionSql =
                    "SELECT " +
                            "    post_range, " +
                            "    COUNT(*) as user_count " +
                            "FROM (" +
                            "    SELECT " +
                            "        nickname, " +
                            "        CASE " +
                            "            WHEN COUNT(*) = 1 THEN '1篇' " +
                            "            WHEN COUNT(*) BETWEEN 2 AND 5 THEN '2-5篇' " +
                            "            WHEN COUNT(*) BETWEEN 6 AND 10 THEN '6-10篇' " +
                            "            WHEN COUNT(*) BETWEEN 11 AND 20 THEN '11-20篇' " +
                            "            ELSE '20篇以上' " +
                            "        END as post_range " +
                            "    FROM tyut_post_data " +
                            "    WHERE del_flag = 0 " +
                            "    GROUP BY nickname" +
                            ") as user_stats " +
                            "GROUP BY post_range " +
                            "ORDER BY " +
                            "    CASE post_range " +
                            "        WHEN '1篇' THEN 1 " +
                            "        WHEN '2-5篇' THEN 2 " +
                            "        WHEN '6-10篇' THEN 3 " +
                            "        WHEN '11-20篇' THEN 4 " +
                            "        ELSE 5 " +
                            "    END ASC";

            List<Map<String, Object>> postDistribution = postDataMapper.executeRawQuery(postDistributionSql);
            analysisData.put("postDistribution", postDistribution);

            // 2. 用户活跃时间段（保持不变）
            QueryWrapper<PostDataEntity> activeTimeWrapper = new QueryWrapper<>();
            activeTimeWrapper.select("HOUR(create_time) as hour, COUNT(*) as count")
                    .groupBy("HOUR(create_time)")
                    .orderByAsc("HOUR(create_time)");
            List<Map<String, Object>> activeTime = postDataMapper.selectMaps(activeTimeWrapper);
            analysisData.put("activeTime", activeTime);

            // 3. 用户互动分析（保持不变）
            QueryWrapper<PostDataEntity> interactionWrapper = new QueryWrapper<>();
            interactionWrapper.select("nickname, " +
                            "SUM(view_count) as total_views, " +
                            "SUM(c_count) as total_comments, " +
                            "SUM(l_count) as total_likes, " +
                            "COUNT(*) as post_count, " +
                            "ROUND(AVG(view_count), 2) as avg_views, " +
                            "ROUND(AVG(c_count), 2) as avg_comments, " +
                            "ROUND(AVG(l_count), 2) as avg_likes")
                    .groupBy("nickname")
                    .having("COUNT(*) >= 5")
                    .orderByDesc("total_views")
                    .last("LIMIT 20");
            List<Map<String, Object>> userInteractions = postDataMapper.selectMaps(interactionWrapper);
            analysisData.put("userInteractions", userInteractions);

            // 4. 新老用户分析 - 使用原生SQL查询
            String userAgeSql =
                    "SELECT " +
                            "    user_type, " +
                            "    COUNT(*) as user_count, " +
                            "    SUM(post_count) as total_posts, " +
                            "    ROUND(AVG(post_count), 2) as avg_posts " +
                            "FROM (" +
                            "    SELECT " +
                            "        nickname, " +
                            "        COUNT(*) as post_count, " +
                            "        CASE " +
                            "            WHEN DATEDIFF(CURDATE(), MIN(create_time)) <= 7 THEN '新用户(7天内)' " +
                            "            WHEN DATEDIFF(CURDATE(), MIN(create_time)) <= 30 THEN '活跃用户(30天内)' " +
                            "            ELSE '老用户(30天以上)' " +
                            "        END as user_type " +
                            "    FROM tyut_post_data " +
                            "    GROUP BY nickname" +
                            ") as user_stats " +
                            "GROUP BY user_type";

            List<Map<String, Object>> userAgeAnalysis = postDataMapper.executeRawQuery(userAgeSql);
            analysisData.put("userAgeAnalysis", userAgeAnalysis);

            return R.success(analysisData);
        } catch (Exception e) {
            log.error("获取用户行为分析数据失败", e);
            return R.error("获取用户行为分析数据失败");
        }
    }

    @Override
    public R getInteractionAnalysis() {
        try {
            Map<String, Object> analysisData = new HashMap<>();

            // 1. 浏览-评论-点赞关系分析 - 使用原生SQL
            String correlationSql =
                    "SELECT " +
                            "    ROUND( " +
                            "        (SUM(view_count * c_count) - SUM(view_count) * SUM(c_count) / COUNT(*)) / " +
                            "        (SQRT(SUM(view_count * view_count) - SUM(view_count) * SUM(view_count) / COUNT(*)) * " +
                            "         SQRT(SUM(c_count * c_count) - SUM(c_count) * SUM(c_count) / COUNT(*))), " +
                            "        4) as view_comment_corr, " +
                            "    ROUND( " +
                            "        (SUM(view_count * l_count) - SUM(view_count) * SUM(l_count) / COUNT(*)) / " +
                            "        (SQRT(SUM(view_count * view_count) - SUM(view_count) * SUM(view_count) / COUNT(*)) * " +
                            "         SQRT(SUM(l_count * l_count) - SUM(l_count) * SUM(l_count) / COUNT(*))), " +
                            "        4) as view_like_corr, " +
                            "    ROUND( " +
                            "        (SUM(c_count * l_count) - SUM(c_count) * SUM(l_count) / COUNT(*)) / " +
                            "        (SQRT(SUM(c_count * c_count) - SUM(c_count) * SUM(c_count) / COUNT(*)) * " +
                            "         SQRT(SUM(l_count * l_count) - SUM(l_count) * SUM(l_count) / COUNT(*))), " +
                            "        4) as comment_like_corr " +
                            "FROM tyut_post_data " +
                            "WHERE del_flag = 0";

            Map<String, Object> correlation = postDataMapper.executeRawQuery(correlationSql).stream()
                    .findFirst().orElse(new HashMap<>());
            analysisData.put("interactionCorrelation", correlation);

            // 2. 互动热度分布 - 使用原生SQL
            String heatDistributionSql =
                    "SELECT " +
                            "    CASE " +
                            "        WHEN view_count < 100 THEN '低热度(<100浏览)' " +
                            "        WHEN view_count BETWEEN 100 AND 500 THEN '中热度(100-500浏览)' " +
                            "        WHEN view_count BETWEEN 501 AND 1000 THEN '较高热度(501-1000浏览)' " +
                            "        ELSE '高热度(>1000浏览)' " +
                            "    END as heat_level, " +
                            "    COUNT(*) as post_count " +
                            "FROM tyut_post_data " +
                            "WHERE del_flag = 0 " +
                            "GROUP BY " +
                            "    CASE " +
                            "        WHEN view_count < 100 THEN '低热度(<100浏览)' " +
                            "        WHEN view_count BETWEEN 100 AND 500 THEN '中热度(100-500浏览)' " +
                            "        WHEN view_count BETWEEN 501 AND 1000 THEN '较高热度(501-1000浏览)' " +
                            "        ELSE '高热度(>1000浏览)' " +
                            "    END " +
                            "ORDER BY " +
                            "    CASE " +
                            "        WHEN view_count < 100 THEN 1 " +
                            "        WHEN view_count BETWEEN 100 AND 500 THEN 2 " +
                            "        WHEN view_count BETWEEN 501 AND 1000 THEN 3 " +
                            "        ELSE 4 " +
                            "    END";

            List<Map<String, Object>> heatDistribution = postDataMapper.executeRawQuery(heatDistributionSql);
            analysisData.put("heatDistribution", heatDistribution);

            // 3. 评论率分析 - 使用原生SQL
            String commentRateSql =
                    "SELECT " +
                            "    CASE " +
                            "        WHEN view_count = 0 THEN '无浏览' " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) = 0 THEN '0%评论率' " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 1 THEN '0-1%评论率' " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 5 THEN '1-5%评论率' " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 10 THEN '5-10%评论率' " +
                            "        ELSE '>10%评论率' " +
                            "    END as comment_rate_range, " +
                            "    COUNT(*) as post_count " +
                            "FROM tyut_post_data " +
                            "WHERE del_flag = 0 " +
                            "GROUP BY " +
                            "    CASE " +
                            "        WHEN view_count = 0 THEN '无浏览' " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) = 0 THEN '0%评论率' " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 1 THEN '0-1%评论率' " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 5 THEN '1-5%评论率' " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 10 THEN '5-10%评论率' " +
                            "        ELSE '>10%评论率' " +
                            "    END " +
                            "ORDER BY " +
                            "    CASE " +
                            "        WHEN view_count = 0 THEN 1 " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) = 0 THEN 2 " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 1 THEN 3 " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 5 THEN 4 " +
                            "        WHEN ROUND(c_count * 100.0 / NULLIF(view_count, 0), 2) <= 10 THEN 5 " +
                            "        ELSE 6 " +
                            "    END";

            List<Map<String, Object>> commentRate = postDataMapper.executeRawQuery(commentRateSql);
            analysisData.put("commentRate", commentRate);

            // 4. 点赞率分析 - 使用原生SQL
            String likeRateSql =
                    "SELECT " +
                            "    CASE " +
                            "        WHEN view_count = 0 THEN '无浏览' " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) = 0 THEN '0%点赞率' " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 0.5 THEN '0-0.5%点赞率' " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 1 THEN '0.5-1%点赞率' " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 2 THEN '1-2%点赞率' " +
                            "        ELSE '>2%点赞率' " +
                            "    END as like_rate_range, " +
                            "    COUNT(*) as post_count " +
                            "FROM tyut_post_data " +
                            "WHERE del_flag = 0 " +
                            "GROUP BY " +
                            "    CASE " +
                            "        WHEN view_count = 0 THEN '无浏览' " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) = 0 THEN '0%点赞率' " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 0.5 THEN '0-0.5%点赞率' " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 1 THEN '0.5-1%点赞率' " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 2 THEN '1-2%点赞率' " +
                            "        ELSE '>2%点赞率' " +
                            "    END " +
                            "ORDER BY " +
                            "    CASE " +
                            "        WHEN view_count = 0 THEN 1 " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) = 0 THEN 2 " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 0.5 THEN 3 " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 1 THEN 4 " +
                            "        WHEN ROUND(l_count * 100.0 / NULLIF(view_count, 0), 2) <= 2 THEN 5 " +
                            "        ELSE 6 " +
                            "    END";

            List<Map<String, Object>> likeRate = postDataMapper.executeRawQuery(likeRateSql);
            analysisData.put("likeRate", likeRate);

            // 5. 高互动帖子特征（使用QueryWrapper，这个没问题）
            QueryWrapper<PostDataEntity> highInteractionWrapper = new QueryWrapper<>();
            highInteractionWrapper.select("cate_name, " +
                            "COUNT(CASE WHEN view_count > 1000 THEN 1 END) as high_view_posts, " +
                            "COUNT(CASE WHEN c_count > 10 THEN 1 END) as high_comment_posts, " +
                            "COUNT(CASE WHEN l_count > 5 THEN 1 END) as high_like_posts")
                    .groupBy("cate_name");
            List<Map<String, Object>> highInteraction = postDataMapper.selectMaps(highInteractionWrapper);
            analysisData.put("highInteraction", highInteraction);

            return R.success(analysisData);
        } catch (Exception e) {
            log.error("获取互动分析数据失败", e);
            return R.error("获取互动分析数据失败");
        }
    }


    @Override
    public R getPredictionAnalysis() {
        try {
            Map<String, Object> analysisData = new HashMap<>();

            // 1. 未来发帖量预测（基于历史趋势）
            QueryWrapper<PostDataEntity> predictionWrapper = new QueryWrapper<>();
            predictionWrapper.select("DATE(create_time) as date, COUNT(*) as count")
                    .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)")
                    .groupBy("DATE(create_time)")
                    .orderByAsc("DATE(create_time)");
            List<Map<String, Object>> historicalData = postDataMapper.selectMaps(predictionWrapper);

            // 简单移动平均预测
            if (historicalData.size() >= 7) {
                List<Map<String, Object>> predictions = new ArrayList<>();
                double last7DaysAvg = historicalData.stream()
                        .skip(Math.max(0, historicalData.size() - 7))
                        .mapToLong(item -> ((Number) item.get("count")).longValue())
                        .average()
                        .orElse(0);

                // 预测未来7天
                for (int i = 1; i <= 7; i++) {
                    Map<String, Object> prediction = new HashMap<>();
                    prediction.put("date", LocalDate.now().plusDays(i).toString());
                    prediction.put("predicted_count", Math.round(last7DaysAvg));
                    prediction.put("confidence", "中");
                    predictions.add(prediction);
                }
                analysisData.put("postPredictions", predictions);
            }

            // 2. 活跃用户预测
            QueryWrapper<PostDataEntity> activeUserPredictionWrapper = new QueryWrapper<>();
            activeUserPredictionWrapper.select("DATE(create_time) as date, COUNT(DISTINCT nickname) as active_users")
                    .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)")
                    .groupBy("DATE(create_time)")
                    .orderByAsc("DATE(create_time)");
            List<Map<String, Object>> activeUserHistory = postDataMapper.selectMaps(activeUserPredictionWrapper);

            if (activeUserHistory.size() >= 7) {
                double last7DaysUserAvg = activeUserHistory.stream()
                        .skip(Math.max(0, activeUserHistory.size() - 7))
                        .mapToLong(item -> ((Number) item.get("active_users")).longValue())
                        .average()
                        .orElse(0);

                analysisData.put("predictedActiveUsers", Math.round(last7DaysUserAvg));
            }

            // 3. 热门分类预测
            QueryWrapper<PostDataEntity> categoryTrendWrapper = new QueryWrapper<>();
            categoryTrendWrapper.select("cate_name, DATE(create_time) as date, COUNT(*) as count")
                    .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)")
                    .groupBy("cate_name, DATE(create_time)")
                    .orderByAsc("DATE(create_time)");
            List<Map<String, Object>> categoryTrends = postDataMapper.selectMaps(categoryTrendWrapper);

            // 分析分类增长趋势
            Map<String, List<Long>> categoryGrowth = new HashMap<>();
            for (Map<String, Object> trend : categoryTrends) {
                String cateName = (String) trend.get("cate_name");
                Long count = ((Number) trend.get("count")).longValue();
                categoryGrowth.computeIfAbsent(cateName, k -> new ArrayList<>()).add(count);
            }

            List<Map<String, Object>> trendingCategories = new ArrayList<>();
            for (Map.Entry<String, List<Long>> entry : categoryGrowth.entrySet()) {
                List<Long> counts = entry.getValue();
                if (counts.size() >= 7) {
                    // 计算最近3天与前3天的增长率
                    double recentAvg = counts.subList(counts.size() - 3, counts.size()).stream()
                            .mapToLong(Long::longValue).average().orElse(0);
                    double previousAvg = counts.subList(counts.size() - 6, counts.size() - 3).stream()
                            .mapToLong(Long::longValue).average().orElse(0);

                    if (previousAvg > 0) {
                        double growthRate = (recentAvg - previousAvg) * 100 / previousAvg;
                        if (growthRate > 10) { // 增长率超过10%
                            Map<String, Object> trending = new HashMap<>();
                            trending.put("cate_name", entry.getKey());
                            trending.put("growth_rate", String.format("%.2f", growthRate));
                            trending.put("trend", growthRate > 20 ? "快速上升" : "稳步上升");
                            trendingCategories.add(trending);
                        }
                    }
                }
            }
            analysisData.put("trendingCategories", trendingCategories);

            // 4. 高峰时段预测
            QueryWrapper<PostDataEntity> peakTimeWrapper = new QueryWrapper<>();
            peakTimeWrapper.select("HOUR(create_time) as hour, COUNT(*) as count")
                    .groupBy("HOUR(create_time)")
                    .orderByDesc("count")
                    .last("LIMIT 3");
            List<Map<String, Object>> peakHours = postDataMapper.selectMaps(peakTimeWrapper);
            analysisData.put("peakHours", peakHours);

            return R.success(analysisData);
        } catch (Exception e) {
            log.error("获取预测分析数据失败", e);
            return R.error("获取预测分析数据失败");
        }
    }


    @Override
    public R getDashboardAnalysis() {
        try {
            Map<String, Object> dashboardData = new HashMap<>();

            // 1. 实时数据
            dashboardData.put("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // 2. 今日关键指标
            QueryWrapper<PostDataEntity> todayWrapper = new QueryWrapper<>();
            todayWrapper.apply("DATE(create_time) = CURDATE()");
            Long todayPosts = postDataMapper.selectCount(todayWrapper);

            QueryWrapper<PostDataEntity> todayStatsWrapper = new QueryWrapper<>();
            todayStatsWrapper.select("SUM(view_count) as today_views, " +
                            "SUM(c_count) as today_comments, " +
                            "SUM(l_count) as today_likes")
                    .apply("DATE(create_time) = CURDATE()");
            Map<String, Object> todayStats = postDataMapper.selectMaps(todayStatsWrapper).stream()
                    .findFirst().orElse(new HashMap<>());

            dashboardData.put("todayMetrics", Map.of(
                    "posts", todayPosts,
                    "views", todayStats.getOrDefault("today_views", 0),
                    "comments", todayStats.getOrDefault("today_comments", 0),
                    "likes", todayStats.getOrDefault("today_likes", 0)
            ));

            // 3. 最近一小时数据
            QueryWrapper<PostDataEntity> lastHourWrapper = new QueryWrapper<>();
            lastHourWrapper.apply("create_time >= DATE_SUB(NOW(), INTERVAL 1 HOUR)");
            Long lastHourPosts = postDataMapper.selectCount(lastHourWrapper);
            dashboardData.put("lastHourPosts", lastHourPosts);

            // 4. 系统健康度
            QueryWrapper<PostDataEntity> healthWrapper = new QueryWrapper<>();
            healthWrapper.select("ROUND(AVG(view_count), 2) as avg_views, " +
                            "ROUND(AVG(c_count), 2) as avg_comments, " +
                            "ROUND(AVG(l_count), 2) as avg_likes, " +
                            "COUNT(DISTINCT nickname) as active_users_today")
                    .apply("DATE(create_time) = CURDATE()");
            Map<String, Object> healthMetrics = postDataMapper.selectMaps(healthWrapper).stream()
                    .findFirst().orElse(new HashMap<>());

            // 计算健康度评分（0-100）
            double healthScore = 0;
            Long avgViews = ((Number) healthMetrics.getOrDefault("avg_views", 0)).longValue();
            Long activeUsers = ((Number) healthMetrics.getOrDefault("active_users_today", 0)).longValue();

            if (avgViews > 100) healthScore += 40;
            else if (avgViews > 50) healthScore += 30;
            else if (avgViews > 20) healthScore += 20;
            else healthScore += 10;

            if (activeUsers > 50) healthScore += 40;
            else if (activeUsers > 20) healthScore += 30;
            else if (activeUsers > 10) healthScore += 20;
            else healthScore += 10;

            if (todayPosts > 50) healthScore += 20;
            else if (todayPosts > 20) healthScore += 15;
            else if (todayPosts > 10) healthScore += 10;
            else healthScore += 5;

            dashboardData.put("healthScore", Math.min(100, healthScore));
            dashboardData.put("healthLevel", healthScore >= 80 ? "健康" :
                    healthScore >= 60 ? "良好" :
                            healthScore >= 40 ? "一般" : "待关注");

            // 5. 异常检测
            QueryWrapper<PostDataEntity> anomalyWrapper = new QueryWrapper<>();
            anomalyWrapper.select("COUNT(*) as zero_interaction_posts")
                    .eq("view_count", 0)
                    .eq("c_count", 0)
                    .eq("l_count", 0)
                    .apply("DATE(create_time) = CURDATE()");
            Map<String, Object> anomalies = postDataMapper.selectMaps(anomalyWrapper).stream()
                    .findFirst().orElse(new HashMap<>());

            Long zeroInteractionPosts = ((Number) anomalies.getOrDefault("zero_interaction_posts", 0)).longValue();
            if (zeroInteractionPosts > 10) {
                dashboardData.put("anomalyAlert", "今日有" + zeroInteractionPosts + "条零互动帖子，建议关注");
            }

            // 6. 建议与洞察
            List<String> insights = new ArrayList<>();

            // 根据数据生成洞察
            if (todayPosts < 10) {
                insights.add("今日发帖量较低，建议关注用户活跃度");
            }

            if (avgViews < 20) {
                insights.add("平均浏览数较低，建议优化内容质量");
            }

            if (activeUsers < 5) {
                insights.add("今日活跃用户较少，建议开展用户激励活动");
            }

            dashboardData.put("insights", insights);

            return R.success(dashboardData);
        } catch (Exception e) {
            log.error("获取仪表盘分析数据失败", e);
            return R.error("获取仪表盘分析数据失败");
        }
    }

    @Override
    public R getContentQualityAnalysis() {
        try {
            Map<String, Object> analysisData = new HashMap<>();

            // 1. 内容长度分析 - 使用原生SQL
            String contentLengthSql =
                    "SELECT " +
                            "    CASE " +
                            "        WHEN LENGTH(content) < 50 THEN '短内容(<50字)' " +
                            "        WHEN LENGTH(content) BETWEEN 50 AND 200 THEN '中等内容(50-200字)' " +
                            "        WHEN LENGTH(content) BETWEEN 201 AND 500 THEN '较长内容(201-500字)' " +
                            "        ELSE '长内容(>500字)' " +
                            "    END as content_length_range, " +
                            "    COUNT(*) as post_count " +
                            "FROM tyut_post_data " +
                            "WHERE del_flag = 0 " +
                            "GROUP BY " +
                            "    CASE " +
                            "        WHEN LENGTH(content) < 50 THEN '短内容(<50字)' " +
                            "        WHEN LENGTH(content) BETWEEN 50 AND 200 THEN '中等内容(50-200字)' " +
                            "        WHEN LENGTH(content) BETWEEN 201 AND 500 THEN '较长内容(201-500字)' " +
                            "        ELSE '长内容(>500字)' " +
                            "    END " +
                            "ORDER BY " +
                            "    CASE " +
                            "        WHEN LENGTH(content) < 50 THEN 1 " +
                            "        WHEN LENGTH(content) BETWEEN 50 AND 200 THEN 2 " +
                            "        WHEN LENGTH(content) BETWEEN 201 AND 500 THEN 3 " +
                            "        ELSE 4 " +
                            "    END";

            List<Map<String, Object>> contentLength = postDataMapper.executeRawQuery(contentLengthSql);
            analysisData.put("contentLength", contentLength);

            // 2. 图片使用分析 - 使用原生SQL
            String imageUsageSql =
                    "SELECT " +
                            "    CASE " +
                            "        WHEN img_paths = '[]' OR img_paths IS NULL OR img_paths = '' THEN '无图片' " +
                            "        ELSE '有图片' " +
                            "    END as has_images, " +
                            "    COUNT(*) as post_count, " +
                            "    ROUND(AVG(view_count), 2) as avg_views, " +
                            "    ROUND(AVG(c_count), 2) as avg_comments, " +
                            "    ROUND(AVG(l_count), 2) as avg_likes " +
                            "FROM tyut_post_data " +
                            "WHERE del_flag = 0 " +
                            "GROUP BY " +
                            "    CASE " +
                            "        WHEN img_paths = '[]' OR img_paths IS NULL OR img_paths = '' THEN '无图片' " +
                            "        ELSE '有图片' " +
                            "    END";

            List<Map<String, Object>> imageUsage = postDataMapper.executeRawQuery(imageUsageSql);
            analysisData.put("imageUsage", imageUsage);

            // 3. 标题长度与互动关系 - 使用原生SQL
            String titleAnalysisSql =
                    "SELECT " +
                            "    CASE " +
                            "        WHEN LENGTH(title) < 10 THEN '短标题(<10字)' " +
                            "        WHEN LENGTH(title) BETWEEN 10 AND 20 THEN '中等标题(10-20字)' " +
                            "        WHEN LENGTH(title) BETWEEN 21 AND 30 THEN '较长标题(21-30字)' " +
                            "        ELSE '长标题(>30字)' " +
                            "    END as title_length_range, " +
                            "    COUNT(*) as post_count, " +
                            "    ROUND(AVG(view_count), 2) as avg_views, " +
                            "    ROUND(AVG(c_count), 2) as avg_comments, " +
                            "    ROUND(AVG(l_count), 2) as avg_likes " +
                            "FROM tyut_post_data " +
                            "WHERE del_flag = 0 " +
                            "GROUP BY " +
                            "    CASE " +
                            "        WHEN LENGTH(title) < 10 THEN '短标题(<10字)' " +
                            "        WHEN LENGTH(title) BETWEEN 10 AND 20 THEN '中等标题(10-20字)' " +
                            "        WHEN LENGTH(title) BETWEEN 21 AND 30 THEN '较长标题(21-30字)' " +
                            "        ELSE '长标题(>30字)' " +
                            "    END " +
                            "ORDER BY " +
                            "    CASE " +
                            "        WHEN LENGTH(title) < 10 THEN 1 " +
                            "        WHEN LENGTH(title) BETWEEN 10 AND 20 THEN 2 " +
                            "        WHEN LENGTH(title) BETWEEN 21 AND 30 THEN 3 " +
                            "        ELSE 4 " +
                            "    END";

            List<Map<String, Object>> titleAnalysis = postDataMapper.executeRawQuery(titleAnalysisSql);
            analysisData.put("titleAnalysis", titleAnalysis);

            // 4. 高质量帖子识别（综合评分）- 这个可以用QueryWrapper
            QueryWrapper<PostDataEntity> qualityPostsWrapper = new QueryWrapper<>();
            qualityPostsWrapper.select("thread_id, title, cate_name, nickname, " +
                            "view_count, c_count, l_count, " +
                            "ROUND((view_count * 0.4 + c_count * 0.3 + l_count * 0.3), 2) as quality_score")
                    .orderByDesc("quality_score")
                    .last("LIMIT 20");
            List<Map<String, Object>> qualityPosts = postDataMapper.selectMaps(qualityPostsWrapper);
            analysisData.put("qualityPosts", qualityPosts);

            // 5. 分类内容质量对比 - 这个可以用QueryWrapper
            QueryWrapper<PostDataEntity> categoryQualityWrapper = new QueryWrapper<>();
            categoryQualityWrapper.select("cate_name, COUNT(*) as post_count, " +
                            "ROUND(AVG(view_count), 2) as avg_views, " +
                            "ROUND(AVG(c_count), 2) as avg_comments, " +
                            "ROUND(AVG(l_count), 2) as avg_likes, " +
                            "ROUND((AVG(view_count) * 0.4 + AVG(c_count) * 0.3 + AVG(l_count) * 0.3), 2) as avg_quality_score")
                    .groupBy("cate_name")
                    .orderByDesc("avg_quality_score");
            List<Map<String, Object>> categoryQuality = postDataMapper.selectMaps(categoryQualityWrapper);
            analysisData.put("categoryQuality", categoryQuality);

            return R.success(analysisData);
        } catch (Exception e) {
            log.error("获取内容质量分析数据失败", e);
            return R.error("获取内容质量分析数据失败");
        }
    }

}
