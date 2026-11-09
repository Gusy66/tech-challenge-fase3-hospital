package com.hospital.notification.controller;

import com.hospital.notification.model.NotificationLog;
import com.hospital.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController          // Marca como controller REST — retorna JSON automaticamente
@RequestMapping("/notifications") // Prefixo da URL — todos os endpoints começam com /notifications
public class NotificationController {

    @Autowired // Injeta o serviço de notificações
    private NotificationService notificationService;

    // GET /notifications — lista todas as notificações registradas
    @GetMapping
    public ResponseEntity<List<NotificationLog>> findAll() {
        List<NotificationLog> logs = notificationService.findAll();
        return ResponseEntity.ok(logs); // Retorna 200 OK com a lista de notificações
    }

    // GET /notifications/status/{status} — lista notificações filtradas por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationLog>> findByStatus(@PathVariable String status) {
        // @PathVariable captura o valor da URL (ex: /notifications/status/ENVIADA)
        List<NotificationLog> logs = notificationService.findByStatus(status);
        return ResponseEntity.ok(logs);
    }
}
