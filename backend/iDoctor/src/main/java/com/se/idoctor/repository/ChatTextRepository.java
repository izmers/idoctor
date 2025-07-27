package com.se.idoctor.repository;

import com.se.idoctor.entity.ChatText;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatTextRepository extends CrudRepository<ChatText, Long> {

    @Query("SELECT c FROM ChatText c WHERE c.chatChannel.id = :channelId ORDER BY c.timestamp ASC")
    List<ChatText> findChatTextsByChannelIdOrderByTimestamp(@Param("channelId") Long channelId);
}
