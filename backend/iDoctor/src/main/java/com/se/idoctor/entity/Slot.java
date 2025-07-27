package com.se.idoctor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.se.idoctor.validation.FreeDayValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "freeDay", "freeTime"}))
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FreeDayValue
    private LocalDate freeDay;

    private LocalTime freeTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor doctor;

    @JsonIgnore
    @OneToOne(mappedBy = "slot", cascade = CascadeType.ALL)
    private Appointment appointment;
}
