package com.tinqinacademy.comments.restexport;
import com.tinqinacademy.comments.api.contracts.operations.admindelete.AdminDeleteOutput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditInput;
import com.tinqinacademy.comments.api.contracts.operations.adminedit.AdminEditOutput;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsOutput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOutput;
import com.tinqinacademy.comments.api.contracts.routes.FeignClientApiRoutes;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Headers("Content-Type: application/json")
public interface CommentsClient {

    @RequestLine(FeignClientApiRoutes.ADMIN_EDIT)
    AdminEditOutput adminEditComment(
            @Param("commentId") String commentId,
            @RequestBody AdminEditInput input);

    @RequestLine(FeignClientApiRoutes.ADMIN_DELETE)
    AdminDeleteOutput adminDeleteComment(
            @Param String commentId);

    @RequestLine(FeignClientApiRoutes.GET_COMMENTS)
    GetCommentsOutput getComments( @Param("roomId") String roomId);

    @RequestLine(FeignClientApiRoutes.LEAVE_COMMENT)
    LeaveCommentOutput leaveComment(
            @Param("roomId") String roomId,
            @RequestBody LeaveCommentInput input);

    @RequestLine(FeignClientApiRoutes.USER_EDIT)
    AdminEditOutput userEditComment(
            @Param("commentId") String commentId,
            @RequestBody AdminEditInput input);
}
