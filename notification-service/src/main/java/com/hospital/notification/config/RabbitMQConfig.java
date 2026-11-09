package com.hospital.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Classe de configuração do Spring — registra beans no contexto
public class RabbitMQConfig {

    // Nome da fila — deve ser o mesmo usado no Producer do scheduling-service
    public static final String QUEUE_NAME = "appointment.notifications";

    @Bean // Declara a fila no RabbitMQ — se já existir, apenas se conecta a ela
    public Queue appointmentQueue() {
        // true = fila durável (sobrevive a reinícios do RabbitMQ)
        return new Queue(QUEUE_NAME, true);
    }
}
