package com.se.idoctor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;
    private String senderId;
    private String recipientId;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "sender_id", referencedColumnName = "id")
//    private Userx senderUser;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
//    private Userx recipientUser;
}
