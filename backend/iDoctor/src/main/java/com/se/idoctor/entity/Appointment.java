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
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @ManyToOne
    private Doctor doctor;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Userx user;

    private String additionalNote;

    @OneToOne
    @JoinColumn(name = "slot_id", referencedColumnName = "id")
    private Slot slot;
}
