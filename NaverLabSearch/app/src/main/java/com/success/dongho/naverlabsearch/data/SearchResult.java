package com.success.dongho.naverlabsearch.data;

import java.util.List;

/**
 * Created by 1002594 on 2017. 11. 22..
 */

public class SearchResult {

    public static class SearchWebResult extends SearchJsonObject {

        //lastBuildDate	datetime	검색 결과를 생성한 시간이다.
        //검색 결과 문서의 총 개수를 의미한다.
        public int total;
        //검색 결과 문서 중, 문서의 시작점을 의미한다.
        public int start;
        //검색된 검색 결과의 개수이다.
        public int display;

        //개별 검색 결과이며 title, link, description을 포함한다.
        public List<Item> items;
        public class Item extends SearchJsonObject{
            //검색 결과 문서의 제목을 나타낸다. 제목에서 검색어와 일치하는 부분은 태그로 감싸져 있다.
            public String title;
            //검색 결과 문서의 하이퍼텍스트 link를 나타낸다.
            public String link;
            //검색 결과 문서의 내용을 요약한 패시지 정보이다. 문서 전체의 내용은 link를 따라가면 읽을 수 있다.
            //패시지에서 검색어와 일치하는 부분은 태그로 감싸져 있다.
            public String description;
        }
    }

    public static class SearchImageResult extends SearchJsonObject {
        //lastBuildDate	datetime	검색 결과를 생성한 시간이다.

        // 검색어
        public String query;
        // item current currentPos
        public int currentPos;

        //검색 결과 문서의 총 개수를 의미한다.
        public int total;
        //검색 결과 문서 중, 문서의 시작점을 의미한다.
        public int start;
        //검색된 검색 결과의 개수이다.
        public int display;

        //개별 검색 결과이며 title, link, thumbnail, sizeheight, sizewidth를 포함한다.
        public List<Item> items;
        public class Item extends SearchJsonObject{
            //검색 결과 이미지의 제목을 나타낸다.
            public String title;
            //검색 결과 이미지의 하이퍼텍스트 link를 나타낸다.
            public String link;
            //검색 결과 이미지의 썸네일 link를 나타낸다.
            public String thumbnail;
            //검색 결과 이미지의 썸네일 높이를 나타낸다.
            public String sizeheight;
            //검색 결과 이미지의 너비를 나타낸다. 단위는 pixel이다.
            public String sizewidth;
        }
    }

    public static class SearchErrorResult extends SearchJsonObject {
        public String errorCode;
        public String errorMessage;
    }
}
