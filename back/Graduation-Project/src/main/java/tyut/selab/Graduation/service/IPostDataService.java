package tyut.selab.Graduation.service;

import com.alibaba.fastjson2.JSONObject;
import tyut.selab.Graduation.domain.PostParam;
import tyut.selab.Graduation.domain.TimeRangeParam;
import tyut.selab.common.domain.R;
import tyut.selab.framework.domain.PageParam;

public interface IPostDataService {

    JSONObject getPostCompleteData(String threadId);

    R getPostDataList(PostParam postParam);

    R getLizardLogList(PageParam pageParam);

    R getNewPostData();

    R getHotPostData();

    R getSummaryData();

    R getPostTrendAnalysis(TimeRangeParam param);

    R getUserBehaviorAnalysis();

    R getInteractionAnalysis();

    R getPredictionAnalysis();

    R getDashboardAnalysis();

    R getContentQualityAnalysis();
}
