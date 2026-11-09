package com.hospital.scheduling.controller;

import com.hospital.scheduling.dto.UserDTO;
import com.hospital.scheduling.model.User;
import com.hospital.scheduling.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController          // Marca como controller REST — retorna JSON automaticamente
@RequestMapping("/auth") // Prefixo da URL — todos os endpoints começam com /auth
public class AuthController {

    @Autowired // Injeta o serviço de usuários
    private UserService userService;

    // POST /auth/register — registra um novo usuário no sistema
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        // @RequestBody converte o JSON da requisição para o objeto UserDTO

        // Chama o serviço para registrar o usuário (com senha criptografada)
        User user = userService.registerUser(userDTO);

        // Monta o DTO de resposta (sem a senha, por segurança)
        UserDTO response = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
        // Não incluímos o password na resposta — nunca retorne senhas na API

        return ResponseEntity.ok(response); // Retorna 200 OK com o usuário criado
    }

    // GET /auth/me — retorna os dados do usuário logado (para testar se a autenticação funciona)
    @GetMapping("/me")
    public ResponseEntity<String> me() {
        return ResponseEntity.ok("Autenticação funcionando!"); // Endpoint público para teste
    }
}
