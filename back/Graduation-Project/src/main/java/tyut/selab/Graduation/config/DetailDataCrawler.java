package tyut.selab.Graduation.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tyut.selab.common.utils.http.HttpClientUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @ClassName: DetailDataCrawler
 * @Description: 帖子详情和评论数据爬取服务
 * @Author: gmslymhn
 * @CreateTime: 2026-04-01
 * @Version: 1.0
 **/
@Slf4j
@Component
public class DetailDataCrawler {

    // 帖子详情接口URL
    private final String THREAD_INFO_URL = "http://api.app.zanao.com/thread/info";

    // 评论列表接口URL
    private final String COMMENT_LIST_URL = "http://api.app.zanao.com/comment/list";

    // 固定密钥
    private final String SECRET_KEY = "16457b3fea31e0a2dcd6c84b40bc2c97";

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
     * 计算字符串的MD5值
     * @param input 输入字符串
     * @return MD5哈希值（大写）
     */
    private String calculateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));

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
     * 生成帖子详情接口的签名
     * 格式：randnum__={X-Sc-Nd}&timestamp__={X-Sc-Td}&secretkey={SECRET_KEY}
     *
     * @param xScNd 随机数
     * @param xScTd 时间戳
     * @return MD5签名
     */
    private String generateThreadInfoSignature(String xScNd, String xScTd) {
        String paramString = String.format(
                "randnum__=%s&timestamp__=%s&secretkey=%s",
                xScNd, xScTd, SECRET_KEY
        );
        return calculateMD5(paramString);
    }

    /**
     * 生成评论列表接口的签名
     * 格式：id={id}&randnum__={X-Sc-Nd}&sign={sign}&timestamp__={X-Sc-Td}&with_hongbao=0&secretkey={SECRET_KEY}
     *
     * @param threadId 帖子ID
     * @param sign 从帖子详情接口获取的sign
     * @param xScNd 随机数
     * @param xScTd 时间戳
     * @return MD5签名
     */
    private String generateCommentListSignature(String threadId, String sign, String xScNd, String xScTd) {
        String paramString = String.format(
                "id=%s&randnum__=%s&sign=%s&timestamp__=%s&with_hongbao=0&secretkey=%s",
                threadId, xScNd, sign, xScTd, SECRET_KEY
        );
        return calculateMD5(paramString);
    }

    /**
     * 生成通用请求头
     *
     * @param xScNd 随机数
     * @param xScTd 时间戳
     * @param xScAh 签名
     * @return 请求头Map
     */
    private Map<String, Object> generateHeaders(String xScNd, String xScTd, String xScAh) {
        Map<String, Object> headers = new HashMap<>();

        headers.put("X-Sc-Nd", xScNd);
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("X-Sc-Td", xScTd);
        headers.put("X-Sc-Hb-V", "180");
        headers.put("X-Sc-Nt-V", "240");
        headers.put("X-Sc-Alias", "tyut");
        headers.put("X-Sc-Client", "app");
        headers.put("X-Sc-Platform", "Android");
        headers.put("X-Sc-Device", "ffffffff-917e-5995-ffff-ffffef05ac4a-b4f5926bb8e6bee8");
        headers.put("X-Sc-Version", "2.4.0");
        headers.put("X-Sc-Token", "Sy9CQ1BzSXk0UEFjaE52TDkvbTUxZ0pER2sxdmlHWXZoUWVGV2ZvOHArOD0%3D");
        headers.put("X-Sc-Ah", xScAh);

        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36");
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Connection", "keep-alive");

        return headers;
    }

    /**
     * 爬取帖子详情数据
     * @param threadId 帖子ID
     * @return JSON格式的帖子详情数据
     */
    public JSONObject crawlThreadInfo(String threadId) {
        try {
            // 生成随机数（X-Sc-Nd）
            String xScNd = generateRandomNumber();

            // 生成当前秒级时间戳（X-Sc-Td）
            long currentTimeSeconds = System.currentTimeMillis() / 1000;
            String xScTd = String.valueOf(currentTimeSeconds);

            // 生成签名（X-Sc-Ah）
            String xScAh = generateThreadInfoSignature(xScNd, xScTd);

            // 生成请求头
            Map<String, Object> headers = generateHeaders(xScNd, xScTd, xScAh);

            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("from", "");
            params.put("id", threadId);

            log.info("开始爬取帖子详情数据，帖子ID: {}", threadId);
            log.debug("请求参数: from=, id={}", threadId);
            log.debug("生成的请求头参数:");
            log.debug("X-Sc-Nd: {}", headers.get("X-Sc-Nd"));
            log.debug("X-Sc-Td: {}", headers.get("X-Sc-Td"));
            log.debug("X-Sc-Ah: {}", headers.get("X-Sc-Ah"));

            // 发送POST请求
            String response = HttpClientUtils.postForm(THREAD_INFO_URL,headers,params);

            if (response != null && !response.isEmpty()) {
                JSONObject jsonResponse = JSON.parseObject(response);
                int errno = jsonResponse.getIntValue("errno");
                String errmsg = jsonResponse.getString("errmsg");

                if (errno == 0) {
                    log.info("成功爬取帖子详情数据");
                    return jsonResponse;
                } else {
                    log.error("帖子详情接口返回错误: errno={}, errmsg={}", errno, errmsg);
                    return createErrorResponse(errno, errmsg);
                }
            } else {
                log.error("帖子详情请求返回空响应");
                return createErrorResponse(-1, "请求返回空响应");
            }

        } catch (Exception e) {
            log.error("爬取帖子详情数据时发生异常: ", e);
            return createErrorResponse(-1, "爬取异常: " + e.getMessage());
        }
    }

    /**
     * 爬取评论列表数据
     * @param threadId 帖子ID
     * @param sign 从帖子详情接口获取的sign
     * @return JSON格式的评论列表数据
     */
    public JSONObject crawlCommentList(String threadId, String sign) {
        try {
            // 生成随机数（X-Sc-Nd）
            String xScNd = generateRandomNumber();

            // 生成当前秒级时间戳（X-Sc-Td）
            long currentTimeSeconds = System.currentTimeMillis() / 1000;
            String xScTd = String.valueOf(currentTimeSeconds);

            // 生成签名（X-Sc-Ah）
            String xScAh = generateCommentListSignature(threadId, sign, xScNd, xScTd);

            // 生成请求头
            Map<String, Object> headers = generateHeaders(xScNd, xScTd, xScAh);

            // 构建请求URL（GET请求，参数在URL中）
            String apiUrl = String.format("%s?with_hongbao=0&sign=%s&id=%s",
                    COMMENT_LIST_URL, sign, threadId);

            log.info("开始爬取评论列表数据，帖子ID: {}", threadId);
            log.debug("请求URL: {}", apiUrl);
            log.debug("生成的请求头参数:");
            log.debug("X-Sc-Nd: {}", headers.get("X-Sc-Nd"));
            log.debug("X-Sc-Td: {}", headers.get("X-Sc-Td"));
            log.debug("X-Sc-Ah: {}", headers.get("X-Sc-Ah"));

            // 发送GET请求
            String response = HttpClientUtils.get(apiUrl, headers, null);

            if (response != null && !response.isEmpty()) {
                JSONObject jsonResponse = JSON.parseObject(response);
                int errno = jsonResponse.getIntValue("errno");
                String errmsg = jsonResponse.getString("errmsg");

                if (errno == 0) {
                    log.info("成功爬取评论列表数据");
                    return jsonResponse;
                } else {
                    log.error("评论列表接口返回错误: errno={}, errmsg={}", errno, errmsg);
                    return createErrorResponse(errno, errmsg);
                }
            } else {
                log.error("评论列表请求返回空响应");
                return createErrorResponse(-1, "请求返回空响应");
            }

        } catch (Exception e) {
            log.error("爬取评论列表数据时发生异常: ", e);
            return createErrorResponse(-1, "爬取异常: " + e.getMessage());
        }
    }

    /**
     * 爬取帖子详情和评论的完整数据
     * @param threadId 帖子ID
     * @return 包含帖子详情和评论的完整JSON数据
     */
    public JSONObject crawlCompleteData(String threadId) {
        JSONObject result = new JSONObject();

        try {
            // 1. 爬取帖子详情
            JSONObject threadInfo = crawlThreadInfo(threadId);

            if (threadInfo.getIntValue("errno") != 0) {
                return threadInfo; // 返回错误信息
            }

            // 2. 从帖子详情中获取sign
            JSONObject data = threadInfo.getJSONObject("data");
            if (data != null) {
                JSONObject detail = data.getJSONObject("detail");
                if (detail != null) {
                    String sign = detail.getString("sign");

                    // 3. 使用获取到的sign爬取评论列表
                    JSONObject commentList = crawlCommentList(threadId, sign);

                    // 4. 合并结果
                    result.put("threadInfo", threadInfo);
                    result.put("commentList", commentList);
                    result.put("errno", 0);
                    result.put("errmsg", "");

                    log.info("成功爬取完整数据，帖子ID: {}", threadId);
                    return result;
                }
            }

            log.error("帖子详情数据中未找到sign字段");
            return createErrorResponse(-1, "帖子详情数据中未找到sign字段");

        } catch (Exception e) {
            log.error("爬取完整数据时发生异常: ", e);
            return createErrorResponse(-1, "爬取完整数据异常: " + e.getMessage());
        }
    }

    /**
     * 创建错误响应
     * @param errno 错误码
     * @param errmsg 错误信息
     * @return JSON错误响应
     */
    private JSONObject createErrorResponse(int errno, String errmsg) {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("errno", errno);
        errorResponse.put("errmsg", errmsg);
        errorResponse.put("data", new JSONObject());
        return errorResponse;
    }

    /**
     * 批量爬取多个帖子的完整数据
     * @param threadIds 帖子ID列表
     * @return 多个帖子的完整数据列表
     */
    public List<JSONObject> batchCrawlCompleteData(List<String> threadIds) {
        List<JSONObject> results = new ArrayList<>();

        for (String threadId : threadIds) {
            JSONObject result = crawlCompleteData(threadId);
            results.add(result);

            // 添加延迟，避免请求过于频繁
            try {
                Thread.sleep(1000); // 1秒延迟
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return results;
    }
}