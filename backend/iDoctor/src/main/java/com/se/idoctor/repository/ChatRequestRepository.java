package com.se.idoctor.repository;

import com.se.idoctor.entity.ChatRequest;
import com.se.idoctor.entity.ChatRequestStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRequestRepository extends CrudRepository<ChatRequest, Long> {

    @Query("SELECT cr FROM ChatRequest cr " +
            "WHERE cr.doctor.user.username = :doctorUsername " +
            "AND cr.chatRequestStatus = :status")
    List<ChatRequest> findByDoctorUsernameAndChatRequestStatus(
            @Param("doctorUsername") String doctorUsername,
            @Param("status") ChatRequestStatus status
    );
    Optional<ChatRequest> findChatRequestByDoctorUserUsernameAndUserUsername(String doctorUsername, String userUsername);
}
