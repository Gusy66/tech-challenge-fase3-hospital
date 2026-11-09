package com.hospital.notification.repository;

import com.hospital.notification.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Marca como repositório Spring — gerencia acesso ao banco de dados
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    // JpaRepository já fornece: save(), findById(), findAll(), deleteById(), etc.

    // Busca notificações pelo status (ENVIADA, FALHA, PENDENTE)
    List<NotificationLog> findByStatus(String status);
}
