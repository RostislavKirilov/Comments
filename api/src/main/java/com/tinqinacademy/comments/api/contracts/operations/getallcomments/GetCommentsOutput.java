package com.tinqinacademy.comments.api.contracts.operations.getallcomments;

import com.tinqinacademy.comments.api.base.OperationOutput;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentsOutput implements OperationOutput {

    private String roomId;
    private String comment;
    private LocalDateTime publishDate;
    private LocalDateTime lastEditTime;
    private String editedBy;
    private String name;
    private List<GetCommentsOutput> comments;


}
