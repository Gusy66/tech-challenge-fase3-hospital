package com.hospital.scheduling.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    private Long id;              // ID da consulta
    private Long doctorId;        // ID do médico responsável
    private String doctorName;    // Nome do médico (para exibição)
    private Long patientId;       // ID do paciente
    private String patientName;   // Nome do paciente (para exibição)
    private String dateTime;      // Data e hora da consulta (formato String para GraphQL)
    private String description;   // Descrição/motivo da consulta
    private String status;        // AGENDADA, REALIZADA ou CANCELADA
}
