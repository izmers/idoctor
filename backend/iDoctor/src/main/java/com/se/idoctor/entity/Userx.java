package com.se.idoctor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Userx {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String fullName;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private Long zip;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    private boolean userIsDoctor;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Doctor doctor;

//    @JsonIgnore
//    @OneToOne(mappedBy = "senderUser", cascade = CascadeType.ALL)
//    private ChatRoom senderChatRoom;
//
//    @JsonIgnore
//    @OneToOne(mappedBy = "recipientUser", cascade = CascadeType.ALL)
//    private ChatRoom recipientChatRoom;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "Userx_Role")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Appointment> appointments;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private Set<ChatChannel> chatChannels;

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private Set<ChatText> sentTexts;

    @JsonIgnore
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private Set<ChatText> receivedTexts;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ChatRequest> chatRequests;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PasswordResetToken passwordResetToken;
}
