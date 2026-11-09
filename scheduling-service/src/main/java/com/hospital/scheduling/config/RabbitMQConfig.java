package com.hospital.scheduling.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Classe de configuração do Spring — registra beans no contexto
public class RabbitMQConfig {

    // Nome da fila — deve ser o mesmo usado no Producer e no Consumer
    public static final String QUEUE_NAME = "appointment.notifications";

    @Bean // Cria a fila no RabbitMQ automaticamente ao iniciar a aplicação
    public Queue appointmentQueue() {
        // true = fila durável (sobrevive a reinícios do RabbitMQ)
        return new Queue(QUEUE_NAME, true);
    }
}
