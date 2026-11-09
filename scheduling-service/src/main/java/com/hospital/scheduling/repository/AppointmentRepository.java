package com.hospital.scheduling.repository;

import com.hospital.scheduling.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository // Repositório para a entidade Appointment
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Busca todas as consultas de um paciente pelo ID
    List<Appointment> findByPatientId(Long patientId);

    // Busca todas as consultas de um médico pelo ID
    List<Appointment> findByDoctorId(Long doctorId);

    // Busca consultas futuras de um paciente (data maior que a data informada)
    List<Appointment> findByPatientIdAndDateTimeAfter(Long patientId, LocalDateTime dateTime);
}
