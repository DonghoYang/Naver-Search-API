package com.success.dongho.naverlabsearch.net.error;

public class SearchErrorCode {

    // 인증에 실패했습니다.
    public static final String CODE_AUTHENTICATION_FAIL         ="024";

    // 잘못된 쿼리요청입니다.
    public static final String CODE_INCORRECT_QUERY_REQUEST     ="SE01";

    // 부적절한 display 값입니다.
    public static final String CODE_INVALID_DISPLAY_VALUE       ="SE02";

    // 부적절한 start 값입니다.
    public static final String CODE_INVALID_START_VALUE         ="SE03";

    // 부적절한 sort 값입니다.
    public static final String CODE_INVALID_SORT_VALUE          ="SE04";

    //잘못된 형식의 인코딩입니다.
    public static final String CODE_MALFORMED_ENCODING          ="SE06";

    // 존재하지 않는 검색 api 입니다.
    public static final String CODE_INVALID_SEARCH_API          ="SE05";

    // 시스템 에러
    public static final String CODE_SYSTEM_ERROR                ="SE99";

    private SearchErrorCode() {
    }
}
