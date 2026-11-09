package com.hospital.notification.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity                              // Marca esta classe como entidade JPA (tabela no banco)
@Table(name = "notification_logs")   // Nome da tabela no banco de dados
@Data                                // Lombok: gera getters, setters, toString, equals e hashCode
@NoArgsConstructor                   // Lombok: gera construtor sem argumentos
@AllArgsConstructor                  // Lombok: gera construtor com todos os argumentos
@Builder                             // Lombok: permite criar objetos com o padrão Builder
public class NotificationLog {

    @Id                              // Marca este campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento no banco
    private Long id;

    @Column(nullable = false, length = 2000) // Conteúdo da mensagem recebida do RabbitMQ
    private String message;

    @Column(nullable = false)        // Data e hora em que a notificação foi recebida
    private LocalDateTime receivedAt;

    @Column(nullable = false)        // Status do envio: ENVIADA, FALHA, PENDENTE
    private String status;
}
