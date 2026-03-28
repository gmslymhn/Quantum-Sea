import authorizationHttp from "@/utils/authorizationHttp.js";

export function getPostDataListApi(postParam) {
  return authorizationHttp.post('/background/post/getPostDataList',
    {
      pageNum: postParam.pageNum,
      pageSize: postParam.pageSize,
      searchContent: postParam.searchContent,
      cateName: postParam.cateName,
      pTime: postParam.pTime
    });
}

export function getLizardLogListApi(pageParam) {
  return authorizationHttp.post('/background/post/getLizardLogList',
    {
      pageNum: pageParam.pageNum,
      pageSize: pageParam.pageSize
    });
}

export function getNewPostDataApi() {
  return authorizationHttp.post('/background/post/getNewPostData');
}

export function getSummaryDataApi() {
  return authorizationHttp.post('/background/post/getSummaryData');
}

export function getContentQualityAnalysisApi() {
  return authorizationHttp.post('/background/post/getContentQualityAnalysis');
}

export function getDashboardAnalysisApi() {
  return authorizationHttp.post('/background/post/getDashboardAnalysis');
}

export function getInteractionAnalysisApi() {
  return authorizationHttp.post('/background/post/getInteractionAnalysis');
}

export function getPostTrendAnalysisApi(params) {
  return authorizationHttp.post('/background/post/getPostTrendAnalysis', params)
}

export function getPredictionAnalysisApi() {
  return authorizationHttp.post('/background/post/getPredictionAnalysis');
}

export function getUserBehaviorAnalysisApi() {
  return authorizationHttp.post('/background/post/getUserBehaviorAnalysis');
}


