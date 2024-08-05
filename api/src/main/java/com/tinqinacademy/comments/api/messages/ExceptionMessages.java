package com.tinqinacademy.comments.api.messages;

import org.springframework.stereotype.Component;

@Component
public class ExceptionMessages {

    private final String COMMENT_NOT_FOUND = "Comment not fouuuund!";
    private final String ROOM_NOT_FOUND = "Room not fouuuund!";

    public String getCommentNotFound() {
        return COMMENT_NOT_FOUND;
    }

    public String getRoomNotFound() {
        return ROOM_NOT_FOUND;
    }
}
