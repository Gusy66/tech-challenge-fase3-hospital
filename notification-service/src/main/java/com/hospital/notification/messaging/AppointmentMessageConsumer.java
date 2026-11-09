package com.hospital.notification.messaging;

import com.hospital.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Marca como serviço gerenciado pelo Spring
public class AppointmentMessageConsumer {

    @Autowired // Injeta o serviço de notificações para processar e salvar o log
    private NotificationService notificationService;

    // Escuta a fila "appointment.notifications" do RabbitMQ
    // Quando o scheduling-service publica uma mensagem, este método é chamado automaticamente
    @RabbitListener(queues = "appointment.notifications")
    public void receiveNotification(String message) {
        System.out.println("Mensagem recebida do RabbitMQ: " + message);

        // Delega o processamento para o serviço (salva log no banco + simula envio)
        notificationService.processNotification(message);
    }
}
