package com.hospital.scheduling.dto;

import com.hospital.scheduling.model.Role;
import lombok.*;

@Data                    // Lombok: gera getters, setters, toString, equals e hashCode
@NoArgsConstructor       // Construtor vazio
@AllArgsConstructor      // Construtor com todos os campos
@Builder                 // Padrão Builder para criação de objetos
public class UserDTO {

    private Long id;             // ID do usuário
    private String email;        // Email (usado para login)
    private String password;     // Senha (só usada no registro, não retornada nas respostas)
    private String name;         // Nome completo
    private Role role;           // MEDICO, ENFERMEIRO ou PACIENTE
}
