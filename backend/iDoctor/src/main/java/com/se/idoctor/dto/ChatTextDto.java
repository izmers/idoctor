package com.se.idoctor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatTextDto {
    private Long id;
    private String senderCred;
    private String recipientCred;
    private String content;
    private Long channelId;
    private Date created;
}
