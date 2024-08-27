package com.tinqinacademy.comments.api.contracts.operations.getallcomments;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentOutput {
    private String roomId;
    private String comment;
    private LocalDateTime publishDate;
    private LocalDateTime lastEditTime;
    private String editedBy;
    private String name;
}
