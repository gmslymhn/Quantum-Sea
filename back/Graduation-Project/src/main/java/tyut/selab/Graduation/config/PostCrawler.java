package tyut.selab.Graduation.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tyut.selab.Graduation.domain.entity.PostDataEntity;
import tyut.selab.common.utils.http.HttpClientUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @ClassName: PostCrawler
 * @Description: 帖子数据爬取服务
 * @Author: gmslymhn
 * @CreateTime: 2026-03-09 22:12
 * @Version: 1.0
 **/
@Slf4j
@Component
public class PostCrawler {

    // 接口基础URL
    private static final String API_BASE_URL = "http://api.app.zanao.com/thread/v2/list";

    // 固定密钥
    private static final String SECRET_KEY = "16457b3fea31e0a2dcd6c84b40bc2c97";

    // 请求头信息 - 改为动态生成
    private Map<String, Object> generateHeaders(String xScNd, String xScTd, String fromTime) {
        Map<String, Object> headers = new HashMap<>();

        // 生成签名（X-Sc-Ah）
        String xScAh = generateSignature(xScNd, xScTd, fromTime);

        // 设置请求头
        headers.put("X-Sc-Nd", xScNd);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("X-Sc-Td", xScTd);
        headers.put("X-Sc-Hb-V", "170");
        headers.put("X-Sc-Nt-V", "236");
        headers.put("X-Sc-Alias", "tyut");
        headers.put("X-Sc-Client", "app");
        headers.put("X-Sc-Platform", "Android");
        headers.put("X-Sc-Device", "ffffffff-917e-5995-ffff-ffffef05ac4a-b4f5926bb8e6bee8");
        headers.put("X-Sc-Version", "2.3.6");
        headers.put("X-Sc-Token", "c3lQL244SDhTTm8xOThIQXdPblZvTUFsV0Nxd1hQcUNzYk9pWTJ6ODIyZz0%3D");
        headers.put("X-Sc-Ah", xScAh);

        // 添加一些通用请求头
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36");
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Connection", "keep-alive");

        return headers;
    }

    /**
     * 生成20位随机数字符串
     * @return 随机数字符串
     */
    private String generateRandomNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成X-Sc-Ah签名
     * 格式：from_time={fromTime}&randnum__={X-Sc-Nd}&timestamp__={X-Sc-Td}&with_comment=true&with_reply=true&secretkey={SECRET_KEY}
     * 然后计算MD5
     *
     * @param xScNd X-Sc-Nd值
     * @param xScTd X-Sc-Td值
     * @param fromTime from_time参数值
     * @return MD5签名
     */
    private String generateSignature(String xScNd, String xScTd, String fromTime) {
        // 构建参数字符串
        String paramString = String.format(
                "from_time=%s&randnum__=%s&timestamp__=%s&with_comment=true&with_reply=true&secretkey=%s",
                fromTime, xScNd, xScTd, SECRET_KEY
        );

        // 计算MD5
        return calculateMD5(paramString);
    }

    /**
     * 计算字符串的MD5值
     * @param input 输入字符串
     * @return MD5哈希值（大写）
     */
    private String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // 转换为十六进制字符串（大写）
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5算法不可用", e);
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 构建完整的API URL
     * @param fromTime from_time参数值
     * @return 完整的API URL
     */
    private String buildApiUrl(String fromTime) {
        return String.format("%s?with_reply=true&from_time=%s&with_comment=true",
                API_BASE_URL, fromTime);
    }

    /**
     * 爬取帖子数据（默认从最新开始）
     * @return 帖子实体列表
     */
    public List<PostDataEntity> crawlPosts() {
        return crawlPosts("0"); // 默认from_time=0
    }

    /**
     * 爬取帖子数据（指定from_time）
     * @param fromTime 起始时间戳（秒级），"0"表示从最新开始
     * @return 帖子实体列表
     */
    public List<PostDataEntity> crawlPosts(String fromTime) {
        List<PostDataEntity> postDataList = new ArrayList<>();

        try {
            // 生成随机数（X-Sc-Nd）
            String xScNd = generateRandomNumber();

            // 生成当前秒级时间戳（X-Sc-Td）
            long currentTimeSeconds = System.currentTimeMillis() / 1000;
            String xScTd = String.valueOf(currentTimeSeconds);

            // 构建完整URL
            String apiUrl = buildApiUrl(fromTime);

            // 动态生成请求头
            Map<String, Object> headers = generateHeaders(xScNd, xScTd, fromTime);

            log.info("开始爬取帖子数据，URL: {}", apiUrl);
            log.debug("请求参数: from_time={}, X-Sc-Nd={}, X-Sc-Td={}", fromTime, xScNd, xScTd);

            // 打印生成的签名信息（调试用）
            log.debug("生成的请求头参数:");
            log.debug("X-Sc-Nd: {}", headers.get("X-Sc-Nd"));
            log.debug("X-Sc-Td: {}", headers.get("X-Sc-Td"));
            log.debug("X-Sc-Ah: {}", headers.get("X-Sc-Ah"));

            // 使用HttpClientUtils发送GET请求
            String response = HttpClientUtils.get(apiUrl, headers, null);

            if (response != null && !response.isEmpty()) {
                // 解析JSON响应
                JSONObject jsonResponse = JSON.parseObject(response);

                // 检查错误码
                int errno = jsonResponse.getIntValue("errno");
                String errmsg = jsonResponse.getString("errmsg");

                if (errno == 0) {
                    // 成功获取数据
                    JSONObject data = jsonResponse.getJSONObject("data");
                    if (data != null) {
                        JSONArray list = data.getJSONArray("list");
                        if (list != null && !list.isEmpty()) {
                            // 遍历帖子列表
                            for (int i = 0; i < list.size(); i++) {
                                JSONObject postJson = list.getJSONObject(i);
                                PostDataEntity postDataEntity = convertToPostEntity(postJson);
                                postDataList.add(postDataEntity);
                            }
                            log.info("成功爬取到 {} 条帖子数据", postDataList.size());
                        } else {
                            log.warn("帖子列表为空");
                        }
                    }
                } else {
                    log.error("接口返回错误: errno={}, errmsg={}", errno, errmsg);
                    // 可以在这里添加重试逻辑或错误处理
                }
            } else {
                log.error("请求返回空响应");
            }

        } catch (Exception e) {
            log.error("爬取帖子数据时发生异常: ", e);
        }

        return postDataList;
    }



    /**
     * 将JSON对象转换为PostEntity
     * @param postJson JSON对象
     * @return PostEntity
     */
    private PostDataEntity convertToPostEntity(JSONObject postJson) {
        PostDataEntity postDataEntity = new PostDataEntity();

        // 设置基本字段
        postDataEntity.setThreadId(postJson.getString("thread_id"));
        postDataEntity.setCateId(postJson.getString("cate_id"));
        postDataEntity.setCateName(postJson.getString("cate_name"));
        postDataEntity.setTitle(postJson.getString("title"));
        postDataEntity.setContent(postJson.getString("content"));

        // 处理图片路径（JSON数组转字符串）
        JSONArray imgPathsArray = postJson.getJSONArray("img_paths");
        if (imgPathsArray != null && !imgPathsArray.isEmpty()) {
            postDataEntity.setImgPaths(imgPathsArray.toJSONString());
        } else {
            postDataEntity.setImgPaths("[]");
        }

        // 设置数值字段
        postDataEntity.setCCount(postJson.getInteger("c_count"));
        postDataEntity.setViewCount(postJson.getInteger("view_count"));
        postDataEntity.setLCount(postJson.getInteger("l_count"));
        postDataEntity.setRewardPrice(postJson.getInteger("reward_price"));

        // 处理时间戳（字符串转Long）
        String pTimeStr = postJson.getString("p_time");
        if (pTimeStr != null && !pTimeStr.isEmpty()) {
            try {
                postDataEntity.setPTime(Long.parseLong(pTimeStr));
            } catch (NumberFormatException e) {
                postDataEntity.setPTime(0L);
            }
        }

        // 设置其他字段
        postDataEntity.setFinishStatus(postJson.getInteger("finish_status"));
        postDataEntity.setHotVal(postJson.getInteger("hot_val"));
        postDataEntity.setHeadimgurl(postJson.getString("headimgurl"));
        postDataEntity.setNickname(postJson.getString("nickname"));
        postDataEntity.setUserLevel(postJson.getInteger("user_level"));
        postDataEntity.setUserLevelTitle(postJson.getString("user_level_title"));
        postDataEntity.setSign(postJson.getString("sign"));

        return postDataEntity;
    }

}
