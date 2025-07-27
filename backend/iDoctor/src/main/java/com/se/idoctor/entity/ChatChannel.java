package com.se.idoctor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "doctor_id"}))
public class ChatChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Userx patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor doctor;

    @JsonIgnore
    @OneToMany(mappedBy = "chatChannel", cascade = CascadeType.ALL)
    private Set<ChatText> chatTexts;

    private String lastMessage;
    private Date dateOfLastMessage;

    @Enumerated(EnumType.STRING)
    private ChatChannelStatus chatChannelStatus;

    private String doctorNote;
}
