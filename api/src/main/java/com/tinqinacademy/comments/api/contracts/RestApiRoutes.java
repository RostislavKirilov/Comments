package com.tinqinacademy.comments.api.contracts;

public class RestApiRoutes {
    private final static String API = "/api/v1";
    public final static String API_HOTEL = API + "/hotel";
    public final static String API_SYSTEM = API + "/system";
    public final static String API_COMMENT = "/comment";
    public static final String ADMIN_EDIT = API_SYSTEM + API_COMMENT + "/{commentId}";
    public static final String ADMIN_DELETE = API_SYSTEM + API_COMMENT + "/{commentId}";
    public static final String GET_COMMENTS = API_HOTEL + "/{roomId}" + API_COMMENT;
    public static final String LEAVE_COMMENT = API_HOTEL + "/{roomId}" + API_COMMENT;
    public static final String USER_EDIT = API_HOTEL + API_COMMENT + "/{commentId}";

}
