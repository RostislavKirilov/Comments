package com.tinqinacademy.comments.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.comments.api.contracts.routes.RestApiRoutesComments;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.editcomment.EditCommentOutput;
import com.tinqinacademy.comments.api.contracts.operations.getallcomments.GetCommentsOutput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentInput;
import com.tinqinacademy.comments.api.contracts.operations.leavecomment.LeaveCommentOutput;
import com.tinqinacademy.comments.persistence.entities.CommentEntity;
import com.tinqinacademy.comments.persistence.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {CommentTestConfig.class})
@ComponentScan(basePackages = {"com.tinqinacademy.comments"})
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID TEST_ROOM_ID = UUID.fromString("eb4928c6-3c01-478c-b0b7-675a87bd8a91");
    @BeforeEach
    void setUp() {
        // Clear the repository before each test
        commentRepository.deleteAll();
    }

    @Test
    void testLeaveCommentSuccessfully() throws Exception {
        // Create a valid LeaveCommentInput
        LeaveCommentInput leaveCommentInput = LeaveCommentInput.builder()
                .roomId(TEST_ROOM_ID.toString())
                .firstName("John")
                .lastName("Doe")
                .comment("This is a test comment.")
                .build();

        // Convert the input to JSON
        String jsonRequest = objectMapper.writeValueAsString(leaveCommentInput);

        // Perform the POST request
        MvcResult result = mockMvc.perform(post(RestApiRoutesComments.LEAVE_COMMENT.replace("{roomId}", TEST_ROOM_ID.toString()))
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())
                //.andExpect(jsonPath())
                .andReturn();

        // Verify the response and database state
        String jsonResponse = result.getResponse().getContentAsString();

        // Deserialize the response to LeaveCommentOutput, not LeaveCommentInput
        LeaveCommentOutput response = objectMapper.readValue(jsonResponse, LeaveCommentOutput.class);

        // Verify that the comment was saved correctly
        assertThat(response.getRoomId()).isEqualTo(leaveCommentInput.getRoomId());
        assertThat(response.getComment()).isEqualTo(leaveCommentInput.getComment());

        // Verify that the comment is saved in the database
        CommentEntity savedComment = commentRepository.findByRoomId(TEST_ROOM_ID).get(0);
        assertThat(savedComment.getRoomId()).isEqualTo(TEST_ROOM_ID);
        assertThat(savedComment.getComment()).isEqualTo(leaveCommentInput.getComment());
    }

    @Test
    void testLeaveCommentInvalidInput() throws Exception {
        // Create an invalid LeaveCommentInput (missing roomId)
        LeaveCommentInput leaveCommentInput = LeaveCommentInput.builder()
                .firstName("John")
                .lastName("Doe")
                .comment("This is a test comment.")
                .build();

        // Convert the input to JSON
        String jsonRequest = objectMapper.writeValueAsString(leaveCommentInput);

        // Perform the POST request
        mockMvc.perform(post(RestApiRoutesComments.LEAVE_COMMENT.replace("{roomId}", TEST_ROOM_ID.toString()))
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEditCommentSuccessfully() throws Exception {
        // First, save a comment to edit
        CommentEntity commentEntity = commentRepository.save(CommentEntity.builder()
                .roomId(TEST_ROOM_ID)
                .firstName("John")
                .lastName("Doe")
                .comment("Original comment.")
                .publishedDate(LocalDateTime.now())
                .build());

        // Create an EditCommentInput with the updated content
        EditCommentInput editCommentInput = EditCommentInput.builder()
                .content("Edited comment.")
                .build();

        // Convert the input to JSON
        String jsonRequest = objectMapper.writeValueAsString(editCommentInput);

        // Perform the PATCH request to edit the comment
        MvcResult result = mockMvc.perform(patch(RestApiRoutesComments.USER_EDIT.replace("{commentId}", commentEntity.getId().toString()))
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the response and database state
        String jsonResponse = result.getResponse().getContentAsString();
        EditCommentOutput response = objectMapper.readValue(jsonResponse, EditCommentOutput.class);

        // Assert the response values
        assertThat(response.getCommentId()).isEqualTo(commentEntity.getId().toString());
        assertThat(response.getContent()).isEqualTo(editCommentInput.getContent());

        // Verify that the comment is updated in the database
        CommentEntity updatedComment = commentRepository.findById(commentEntity.getId()).orElse(null);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getComment()).isEqualTo(editCommentInput.getContent());
    }


    @Test
    void testGetAllCommentsForRoom() throws Exception {
        // Save multiple comments for the room
        commentRepository.save(CommentEntity.builder()
                .roomId(TEST_ROOM_ID)
                .firstName("John")
                .lastName("Doe")
                .comment("First comment.")
                .publishedDate(LocalDateTime.now())
                .build());

        commentRepository.save(CommentEntity.builder()
                .roomId(TEST_ROOM_ID)
                .firstName("Jane")
                .lastName("Doe")
                .comment("Second comment.")
                .publishedDate(LocalDateTime.now())
                .build());

        MvcResult result = mockMvc.perform(get(RestApiRoutesComments.GET_COMMENTS.replace("{roomId}", TEST_ROOM_ID.toString())))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        GetCommentsOutput response = objectMapper.readValue(jsonResponse, GetCommentsOutput.class);

        assertThat(response.getComments()).hasSize(2);
        assertThat(response.getComments().get(0).getComment()).isEqualTo("First comment.");
        assertThat(response.getComments().get(1).getComment()).isEqualTo("Second comment.");
    }

    @Test
    void testEditCommentNotFound() throws Exception {
        // Create an EditCommentInput with the updated content
        EditCommentInput editCommentInput = EditCommentInput.builder()
                .content("Edited comment.")
                .build();

        // Convert the input to JSON
        String jsonRequest = objectMapper.writeValueAsString(editCommentInput);

        // Perform the PATCH request to edit a non-existent comment
        MvcResult result = mockMvc.perform(patch(RestApiRoutesComments.USER_EDIT.replace("{commentId}", UUID.randomUUID().toString()))
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Optionally, you can verify the response body
        String jsonResponse = result.getResponse().getContentAsString();
        assertThat(jsonResponse).contains("Comment not found");
    }



    //@ParameterizedTest
    //@ValueSource(strings = )
    @Test
    void testEditCommentWithInvalidData() throws Exception {
        // First, save a comment to edit
        CommentEntity commentEntity = commentRepository.save(CommentEntity.builder()
                .roomId(TEST_ROOM_ID)
                .firstName("John")
                .lastName("Doe")
                .comment("Valid comment")
                .publishedDate(LocalDateTime.now())
                .build());

        String longContent = "A".repeat(1001);
        EditCommentInput editCommentInput = EditCommentInput.builder()
                .content(longContent)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(editCommentInput);

        MvcResult result = mockMvc.perform(patch(RestApiRoutesComments.USER_EDIT.replace("{commentId}", commentEntity.getId().toString()))
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        assertThat(jsonResponse).contains("");
    }

    @Test
    void testEditCommentWithNoChange() throws Exception {
        CommentEntity commentEntity = commentRepository.save(CommentEntity.builder()
                .roomId(TEST_ROOM_ID)
                .firstName("John")
                .lastName("Doe")
                .comment("Original comment.")
                .publishedDate(LocalDateTime.now())
                .build());

        EditCommentInput editCommentInput = EditCommentInput.builder()
                .content("Original comment.")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(editCommentInput);

        MvcResult result = mockMvc.perform(patch(RestApiRoutesComments.USER_EDIT.replace("{commentId}", commentEntity.getId().toString()))
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        EditCommentOutput response = objectMapper.readValue(jsonResponse, EditCommentOutput.class);

        assertThat(response.getCommentId()).isEqualTo(commentEntity.getId().toString());
        assertThat(response.getContent()).isEqualTo(editCommentInput.getContent());

        CommentEntity updatedComment = commentRepository.findById(commentEntity.getId()).orElse(null);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getComment()).isEqualTo(editCommentInput.getContent());
    }
}
