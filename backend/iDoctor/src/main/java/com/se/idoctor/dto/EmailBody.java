package com.se.idoctor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailBody {
    private String host;
    private int port;

    private String username;
    private String password;
    private String to;

    private String subject;
    private String body;
}
