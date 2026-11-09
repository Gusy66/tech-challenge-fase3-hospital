package com.hospital.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Anotação principal — habilita auto-configuração, component scan e configuração do Spring Boot
public class NotificationApplication {

    // Método main — ponto de entrada da aplicação
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args); // Inicia o Spring Boot
    }
}
