package com.tinqinacademy.comments.persistence.repository;

import com.tinqinacademy.comments.persistence.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    List<CommentEntity> findByRoomId( UUID roomId);
    Optional<CommentEntity> findByIdAndRoomId( UUID id, UUID roomId);

}
