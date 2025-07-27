package com.se.idoctor.repository;

import com.se.idoctor.entity.ChatChannel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ChatChannelRepository extends CrudRepository<ChatChannel, Long> {
    List<ChatChannel> findByPatientUsernameOrPatientEmail(String username, String email);
    List<ChatChannel> findByDoctorUserUsernameOrDoctorUserEmail(String username, String email);
    Optional<ChatChannel> findChatChannelByDoctorUserUsernameAndPatientUsername(String doctorUsername, String patientUsername);
}
