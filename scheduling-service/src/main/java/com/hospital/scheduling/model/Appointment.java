package com.hospital.scheduling.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity                          // Entidade JPA — representa a tabela "appointments"
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)   // Relacionamento N:1 — várias consultas para 1 médico
    @JoinColumn(name = "doctor_id", nullable = false) // Coluna FK no banco
    private User doctor;                 // Médico responsável pela consulta

    @ManyToOne(fetch = FetchType.LAZY)   // Relacionamento N:1 — várias consultas para 1 paciente
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;                // Paciente da consulta

    @Column(nullable = false)            // Data e hora da consulta
    private LocalDateTime dateTime;

    @Column(length = 1000)               // Descrição/motivo da consulta (até 1000 caracteres)
    private String description;

    @Column(nullable = false)            // Status da consulta
    private String status;               // AGENDADA, REALIZADA, CANCELADA
}
