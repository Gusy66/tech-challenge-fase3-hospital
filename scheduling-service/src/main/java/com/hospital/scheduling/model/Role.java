package com.hospital.scheduling.model;

// Enum que define os 3 tipos de usuário do sistema
public enum Role {
    MEDICO,      // Pode visualizar e editar histórico de consultas
    ENFERMEIRO,  // Pode registrar consultas e acessar histórico
    PACIENTE     // Pode visualizar apenas suas próprias consultas
}
