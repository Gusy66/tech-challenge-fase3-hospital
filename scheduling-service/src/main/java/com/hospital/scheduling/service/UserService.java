package com.hospital.scheduling.service;

import com.hospital.scheduling.dto.UserDTO;
import com.hospital.scheduling.model.User;
import com.hospital.scheduling.model.Role;
import com.hospital.scheduling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service // Marca como serviço gerenciado pelo Spring
public class UserService implements UserDetailsService {
    // Implementa UserDetailsService — interface obrigatória para Spring Security autenticar usuários

    @Autowired // Injeta o repositório de usuários
    private UserRepository userRepository;

    @Autowired // Injeta o codificador de senhas (BCrypt definido no SecurityConfig)
    private PasswordEncoder passwordEncoder;

    // Método chamado automaticamente pelo Spring Security durante o login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário no banco pelo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Converte a Role do nosso sistema para uma Authority do Spring Security
        // Prefixo "ROLE_" é obrigatório para usar @PreAuthorize("hasRole('MEDICO')")
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        // Retorna um UserDetails do Spring Security com email, senha e permissões
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),                          // Username (usamos email)
                user.getPassword(),                       // Senha (hash BCrypt)
                Collections.singletonList(authority)       // Lista de permissões (roles)
        );
    }

    // Registra um novo usuário no sistema
    public User registerUser(UserDTO userDTO) {
        // Verifica se já existe um usuário com este email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + userDTO.getEmail());
        }

        // Constrói a entidade User a partir do DTO
        User user = User.builder()
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword())) // Criptografa a senha com BCrypt
                .name(userDTO.getName())
                .role(userDTO.getRole())
                .build();

        // Salva no banco e retorna o usuário criado
        return userRepository.save(user);
    }

    // Lista todos os usuários cadastrados
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Busca um usuário pelo ID
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }
}
