package com.balancerx.api.controller;

import com.balancerx.api.dto.LoginRequest;
import com.balancerx.api.dto.LoginResponse;
import com.balancerx.domain.model.Usuario;
import com.balancerx.domain.repository.UsuarioRepository;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        String token = generarToken(usuario.getId(), usuario.getRol().name());
        return ResponseEntity.ok(new LoginResponse(token, token));
    }

    private String generarToken(UUID userId, String rol) {
        String payload = userId + ":" + rol + ":" + Instant.now();
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }
}
