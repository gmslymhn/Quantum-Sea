package tyut.selab.Graduation.service;

import tyut.selab.Graduation.domain.PostParam;
import tyut.selab.Graduation.domain.TimeRangeParam;
import tyut.selab.common.domain.R;
import tyut.selab.framework.domain.PageParam;

public interface IPostDataService {

    R getPostDataList(PostParam postParam);

    R getLizardLogList(PageParam pageParam);

    R getNewPostData();

    R getSummaryData();

    R getPostTrendAnalysis(TimeRangeParam param);

    R getUserBehaviorAnalysis();

    R getInteractionAnalysis();

    R getPredictionAnalysis();

    R getDashboardAnalysis();

    R getContentQualityAnalysis();
}
