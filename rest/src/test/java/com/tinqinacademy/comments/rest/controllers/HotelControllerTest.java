package com.tinqinacademy.comments.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.comments.api.contracts.HotelService;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentOutput;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsOutput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOutput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.tinqinacademy.comments.api.contracts.RestApiRoutes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HotelControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HotelService hotelService;

    @Test
    public void testGetAllComments () throws Exception {
        String roomId = "123";

        List<GetCommentsOutput> mockComments = new ArrayList<>();
        mockComments.add(GetCommentsOutput.builder()
                .roomId(roomId)
                .comment("Great room!")
                .publishDate(LocalDateTime.now())
                .lastEditTime(LocalDateTime.now())
                .editedBy("user1")
                .build());
        mockComments.add(GetCommentsOutput.builder()
                .roomId(roomId)
                .comment("Had a pleasant stay.")
                .publishDate(LocalDateTime.now())
                .lastEditTime(LocalDateTime.now())
                .editedBy("user2")
                .build());

        when(hotelService.getAllComments(roomId)).thenReturn(mockComments);

        mockMvc.perform(get(GET_COMMENTS, roomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].comment").value("Great room!"))
                .andExpect(jsonPath("$[0].editedBy").value("user1"))
                .andExpect(jsonPath("$[1].comment").value("Had a pleasant stay."))
                .andExpect(jsonPath("$[1].editedBy").value("user2"));
    }

    @Test
    public void testLeaveComment_whenValidInput_thenReturns200 () throws Exception {
        LeaveCommentInput input = LeaveCommentInput.builder()
                .roomId("123")
                .firstName("Jon")
                .lastName("Doe")
                .comment("This is a comment.")
                .build();

        LeaveCommentOutput output = LeaveCommentOutput.builder()
                .roomId("123")
                .comment("This is a comment.")
                .build();

        when(hotelService.leaveComment(any(LeaveCommentInput.class))).thenReturn(output);

        mockMvc.perform(post(LEAVE_COMMENT, "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value("123"))
                .andExpect(jsonPath("$.comment").value("This is a comment."));
    }

    @Test
    public void testLeaveComment_whenInvalidInput_thenReturns400 () throws Exception {
        LeaveCommentInput input = LeaveCommentInput.builder()
                .roomId("")
                .firstName("")
                .lastName("")
                .comment("")
                .build();

        mockMvc.perform(post(LEAVE_COMMENT, "defaultRoomId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void editComment_whenValidDataInput_return200 () throws Exception {
        EditCommentInput input = EditCommentInput.builder()
                .content("Some test content")
                .roomId("101a")
                .build();

        EditCommentOutput output = EditCommentOutput.builder()
                .commentId("123")
                .content("Some test content")
                .roomId("101a")
                .build();

        when(hotelService.updateContentComment(any(EditCommentInput.class))).thenReturn(output);

        mockMvc.perform(patch(USER_EDIT, "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.commentId").value("123"))
                .andExpect(jsonPath("$.content").value("Some test content"))
                .andExpect(jsonPath("$.roomId").value("101a"));
    }

    @Test
    void editComment_whenInvalidDataInput_return400 () throws Exception {
        EditCommentInput editCommentInput = EditCommentInput.builder()
                .content("A".repeat(1001))
                .roomId("InvalidRoomId")
                .build();

        mockMvc.perform(patch(USER_EDIT, "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editCommentInput)))
                .andExpect(status().isBadRequest());
    }*/
}
