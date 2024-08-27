package com.tinqinacademy.comments.api.contracts.routes;

public class FeignClientApiRoutes {

    public static final String ADMIN_EDIT = "PATCH " + RestApiRoutesComments.ADMIN_EDIT;
    public static final String ADMIN_DELETE = "DELETE " + RestApiRoutesComments.ADMIN_DELETE;
    public static final String GET_COMMENTS = "GET " + RestApiRoutesComments.GET_COMMENTS;
    //public static final String GET_COMMENTS = "GET " + RestApiRoutesComments.GET_COMMENTS + "?roomId={roomId}";

    public static final String LEAVE_COMMENT = "POST " + RestApiRoutesComments.LEAVE_COMMENT;
    public static final String USER_EDIT = "PATCH " + RestApiRoutesComments.USER_EDIT;

}