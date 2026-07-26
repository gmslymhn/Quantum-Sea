package tyut.selab.Graduation.controller;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tyut.selab.Graduation.domain.PostParam;
import tyut.selab.Graduation.domain.TimeRangeParam;
import tyut.selab.Graduation.service.IPostDataService;
import tyut.selab.common.domain.R;
import tyut.selab.framework.domain.PageParam;

/**
 * @ClassName: PostDataController
 * @Description:
 * @Author: gmslymhn
 * @CreateTime: 2026-03-26 09:16
 * @Version: 1.0
 **/
@RestController
@Tag(name = "爬取帖子管理")
@RequestMapping("/background/post")
@Slf4j
public class PostDataController {
    @Autowired
    private IPostDataService iPostDataService;


    @PostMapping("/getPostDataList")
    public R getPostDataList(@RequestBody @Validated PostParam postParam){
        return iPostDataService.getPostDataList(postParam);
    }
    @PostMapping("/getLizardLogList")
    public R getLizardLogList(@RequestBody @Validated PageParam pageParam){
        return iPostDataService.getLizardLogList(pageParam);
    }
    @PostMapping("/getNewPostData")
    public R getNewPostData(){
        return iPostDataService.getNewPostData();
    }

    @PostMapping("/getSummaryData")
    public R getSummaryData(){
        return iPostDataService.getSummaryData();
    }
    @PostMapping("/getPostTrendAnalysis")
    @Operation(summary = "时间趋势分析")
    public R getPostTrendAnalysis(@RequestBody @Validated TimeRangeParam param){
        return iPostDataService.getPostTrendAnalysis(param);
    }
    @PostMapping("/getUserBehaviorAnalysis")
    @Operation(summary = "用户行为分析")
    public R getUserBehaviorAnalysis(){
        return iPostDataService.getUserBehaviorAnalysis();
    }
    @PostMapping("/getContentQualityAnalysis")
    @Operation(summary = "内容质量分析")
    public R getContentQualityAnalysis(){
        return iPostDataService.getContentQualityAnalysis();
    }
    @PostMapping("/getInteractionAnalysis")
    @Operation(summary = "互动关系分析")
    public R getInteractionAnalysis(){
        return iPostDataService.getInteractionAnalysis();
    }
    @PostMapping("/getPredictionAnalysis")
    @Operation(summary = "预测分析")
    public R getPredictionAnalysis(){
        return iPostDataService.getPredictionAnalysis();
    }
    @PostMapping("/getDashboardAnalysis")
    @Operation(summary = "综合仪表盘")
    public R getDashboardAnalysis(){
        return iPostDataService.getDashboardAnalysis();
    }

    /**
     * 获取热点帖子数据（默认参数）
     */
    @PostMapping("/getHotPostData")
    public R getHotPostData() {
        return iPostDataService.getHotPostData();
    }

    @GetMapping("/getPostDetail")
    public JSONObject getPostDetail(@RequestParam String threadId) {
        return iPostDataService.getPostCompleteData(threadId);
    }

}
