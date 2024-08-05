package com.tinqinacademy.comments.api.contracts.operations.leavecomment;

import com.tinqinacademy.comments.api.base.OperationOutput;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LeaveCommentOutput implements OperationOutput {

    private String roomId;
    private String comment;
    private LocalDateTime publishedDate;
    private LocalDateTime lastEditBy;
    private String editedBy;

}
