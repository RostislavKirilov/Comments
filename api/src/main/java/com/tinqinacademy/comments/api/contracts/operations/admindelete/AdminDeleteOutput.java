package com.tinqinacademy.comments.api.contracts.operations.admindelete;

import com.tinqinacademy.comments.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminDeleteOutput implements OperationOutput {
    private String commentId;
    private String message;
}
