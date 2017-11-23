package com.success.dongho.naverlabsearch.data;

/**
 * Created by 1002594 on 2017. 11. 22..
 */

public class SearchRequestParam {
    private SearchRequestParam(){}

    //검색을 원하는 문자열로서 UTF-8로 인코딩
    public static final String QUERY = "query";

    //검색 결과 출력 건수 지정 10(기본값), 100(최대)
    public static final String DISPLAY = "display";

    //검색 시작 위치로 최대 1000까지 가능 1(기본값), 1000(최대)
    public static final String START = "start";

    //정렬 옵션: sim (유사도순), date (날짜순)
    public static final String SORT = "sort";

    //사이즈 필터 옵션: all(전체)(default), large(큰 사이즈), medium(중간 사이즈), small(작은 사이즈)
    public static final String FILTER = "filter";

    public static final String NAVER_CLIENT_ID = "X-Naver-Client-Id";
    public static final String NAVER_CLIENT_SECRET = "X-Naver-Client-Secret";

    public static final String INDEX = "currentPos";
    public static final String FILE_NAME = "file_name";

}
