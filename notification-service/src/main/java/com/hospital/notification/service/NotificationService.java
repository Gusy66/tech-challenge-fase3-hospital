package com.hospital.notification.service;

import com.hospital.notification.model.NotificationLog;
import com.hospital.notification.repository.NotificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service // Marca como serviço gerenciado pelo Spring — contém a lógica de negócio
public class NotificationService {

    @Autowired // Injeta o repositório de logs de notificação
    private NotificationLogRepository notificationLogRepository;

    // Processa uma notificação recebida do RabbitMQ e salva o log no banco
    public NotificationLog processNotification(String message) {
        // Simula o envio da notificação (email, SMS, push notification, etc.)
        System.out.println("Processando notificação: " + message);

        // Cria o registro de log da notificação
        NotificationLog log = NotificationLog.builder()
                .message(message)                       // Mensagem recebida do RabbitMQ
                .receivedAt(LocalDateTime.now())         // Data/hora atual do recebimento
                .status("ENVIADA")                       // Marca como enviada (simulação)
                .build();

        // Salva o log no banco de dados e retorna
        return notificationLogRepository.save(log);
    }

    // Lista todas as notificações registradas
    public List<NotificationLog> findAll() {
        return notificationLogRepository.findAll();
    }

    // Lista notificações filtradas por status
    public List<NotificationLog> findByStatus(String status) {
        return notificationLogRepository.findByStatus(status);
    }
}
