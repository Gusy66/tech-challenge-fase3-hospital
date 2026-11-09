package com.hospital.scheduling.repository;

import com.hospital.scheduling.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository // Marca como repositório Spring — gerencia acesso ao banco de dados
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository já fornece: save(), findById(), findAll(), deleteById(), etc.

    // Busca um usuário pelo email — retorna Optional (pode ou não existir)
    Optional<User> findByEmail(String email);

    // Verifica se já existe um usuário com este email — retorna true/false
    boolean existsByEmail(String email);
}
