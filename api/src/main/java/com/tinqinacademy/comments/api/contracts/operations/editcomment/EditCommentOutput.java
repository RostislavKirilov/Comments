package com.tinqinacademy.comments.api.contracts.operations.editcomment;

import com.tinqinacademy.comments.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EditCommentOutput implements OperationOutput {

    private String commentId;
    private String content;
    private String roomId;

}
