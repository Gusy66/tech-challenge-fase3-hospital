package com.hospital.scheduling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity                          // Marca esta classe como entidade JPA (tabela no banco)
@Table(name = "users")           // Define o nome da tabela no banco de dados
@Data                            // Lombok: gera getters, setters, toString, equals e hashCode
@NoArgsConstructor               // Lombok: gera construtor sem argumentos
@AllArgsConstructor              // Lombok: gera construtor com todos os argumentos
@Builder                         // Lombok: permite criar objetos com o padrão Builder
public class User {

    @Id                          // Marca este campo como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento no banco
    private Long id;

    @Column(nullable = false, unique = true) // Campo obrigatório e único
    private String email;

    @Column(nullable = false)    // Campo obrigatório — senha será armazenada como hash
    private String password;

    @Column(nullable = false)    // Nome completo do usuário
    private String name;

    @Enumerated(EnumType.STRING) // Armazena o enum como texto no banco (ex: "MEDICO")
    @Column(nullable = false)    // Role é obrigatória
    private Role role;           // MEDICO, ENFERMEIRO ou PACIENTE
}
