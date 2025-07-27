package com.se.idoctor.dto;

import com.se.idoctor.entity.ChatRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDto {
    private Long id;
    private String userUsername;
    private String doctorUsername;
    private String userNote;
    private String doctorNote;
    private ChatRequestStatus chatRequestStatus;
}
