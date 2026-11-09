package com.hospital.scheduling.service;

import com.hospital.scheduling.dto.AppointmentDTO;
import com.hospital.scheduling.messaging.AppointmentMessageProducer;
import com.hospital.scheduling.model.Appointment;
import com.hospital.scheduling.model.User;
import com.hospital.scheduling.repository.AppointmentRepository;
import com.hospital.scheduling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service // Serviço que contém a lógica de negócio para consultas médicas
public class AppointmentService {

    @Autowired // Repositório de consultas
    private AppointmentRepository appointmentRepository;

    @Autowired // Repositório de usuários (para buscar médico e paciente)
    private UserRepository userRepository;

    @Autowired // Producer do RabbitMQ (para enviar notificações)
    private AppointmentMessageProducer messageProducer;

    // Formato de data/hora usado nas requisições: "2024-12-25T10:30:00"
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Cria uma nova consulta médica
    public Appointment createAppointment(Long doctorId, Long patientId, String dateTime, String description) {
        // Busca o médico pelo ID — lança exceção se não encontrar
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado com ID: " + doctorId));

        // Busca o paciente pelo ID
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com ID: " + patientId));

        // Constrói a entidade Appointment
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .dateTime(LocalDateTime.parse(dateTime, FORMATTER)) // Converte String para LocalDateTime
                .description(description)
                .status("AGENDADA")  // Toda nova consulta começa como AGENDADA
                .build();

        // Salva no banco de dados
        Appointment saved = appointmentRepository.save(appointment);

        // Envia mensagem para o RabbitMQ notificando sobre a nova consulta
        messageProducer.sendAppointmentNotification(
                "Nova consulta agendada — Paciente: " + patient.getName()
                + " | Médico: " + doctor.getName()
                + " | Data: " + dateTime
        );

        return saved;
    }

    // Atualiza uma consulta existente
    public Appointment updateAppointment(Long id, String dateTime, String description, String status) {
        // Busca a consulta pelo ID
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada com ID: " + id));

        // Atualiza apenas os campos que foram informados (não nulos)
        if (dateTime != null) {
            appointment.setDateTime(LocalDateTime.parse(dateTime, FORMATTER));
        }
        if (description != null) {
            appointment.setDescription(description);
        }
        if (status != null) {
            appointment.setStatus(status);
        }

        // Salva as alterações no banco
        Appointment updated = appointmentRepository.save(appointment);

        // Envia mensagem para o RabbitMQ notificando sobre a alteração
        messageProducer.sendAppointmentNotification(
                "Consulta atualizada (ID: " + id + ")"
                + " | Paciente: " + appointment.getPatient().getName()
                + " | Novo status: " + appointment.getStatus()
        );

        return updated;
    }

    // Busca todas as consultas de um paciente
    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    // Busca consultas futuras de um paciente (a partir de agora)
    public List<Appointment> findFutureByPatientId(Long patientId) {
        return appointmentRepository.findByPatientIdAndDateTimeAfter(patientId, LocalDateTime.now());
    }

    // Busca todas as consultas (para médicos e enfermeiros)
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }
}
