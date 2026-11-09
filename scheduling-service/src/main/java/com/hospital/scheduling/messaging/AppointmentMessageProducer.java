package com.hospital.scheduling.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Marca como serviço gerenciado pelo Spring
public class AppointmentMessageProducer {

    @Autowired // Injeta o template do RabbitMQ para enviar mensagens
    private RabbitTemplate rabbitTemplate;

    // Nome da fila onde as mensagens serão publicadas
    private static final String QUEUE_NAME = "appointment.notifications";

    // Envia uma mensagem para a fila do RabbitMQ quando uma consulta é criada/editada
    public void sendAppointmentNotification(String message) {
        rabbitTemplate.convertAndSend(QUEUE_NAME, message); // Publica a mensagem na fila
    }
}
