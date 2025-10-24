package com.balancerx.model.service.impl;

import com.balancerx.model.entity.Usuario;
import com.balancerx.model.service.UsuarioService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación del servicio de usuarios.
 * Esta implementación utiliza datos en memoria para la interfaz JavaFX.
 */
public class UsuarioServiceImpl implements UsuarioService {
    
    private final List<Usuario> usuarios;
    private final AtomicLong idGenerator;
    
    public UsuarioServiceImpl() {
        this.usuarios = new ArrayList<>();
        this.idGenerator = new AtomicLong(1);
        
        // Inicializar con datos de ejemplo
        inicializarDatos();
    }
    
    private void inicializarDatos() {
        usuarios.add(new Usuario(idGenerator.getAndIncrement(), "Administrador", "admin@balancerx.com", "ADMIN", "hash_admin123", true, LocalDateTime.now()));
        usuarios.add(new Usuario(idGenerator.getAndIncrement(), "Elaborador Principal", "elaborador@balancerx.com", "ELABORADOR", "hash_elab123", true, LocalDateTime.now()));
        usuarios.add(new Usuario(idGenerator.getAndIncrement(), "Usuario Banco", "banco@balancerx.com", "BANCO", "hash_banco123", true, LocalDateTime.now()));
        usuarios.add(new Usuario(idGenerator.getAndIncrement(), "Asignador", "asignador@balancerx.com", "ASIGNADOR", "hash_asig123", true, LocalDateTime.now()));
        usuarios.add(new Usuario(idGenerator.getAndIncrement(), "Auditor", "auditor@balancerx.com", "AUDITOR", "hash_audit123", true, LocalDateTime.now()));
    }
    
    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        if (existePorEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }
        
        Usuario nuevo = new Usuario(
            idGenerator.getAndIncrement(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol(),
            "hash_" + usuario.getHashPassword(), // Simular hash de contraseña
            usuario.isActivo(),
            LocalDateTime.now()
        );
        
        usuarios.add(nuevo);
        return nuevo;
    }
    
    @Override
    public Optional<Usuario> autenticarUsuario(String email, String password) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .filter(u -> u.isActivo())
                .filter(u -> verificarPassword(u.getHashPassword(), password))
                .findFirst();
    }
    
    private boolean verificarPassword(String hashPassword, String password) {
        // Simulación simple de verificación de contraseña
        // En un caso real, aquí se verificaría el hash
        return hashPassword.equals("hash_" + password) || 
               hashPassword.equals(password) ||
               (password.equals("admin123") && hashPassword.contains("admin")) ||
               (password.equals("user123") && (hashPassword.contains("elab") || hashPassword.contains("banco") || hashPassword.contains("asig") || hashPassword.contains("audit")));
    }
    
    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return new ArrayList<>(usuarios);
    }
    
    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        Optional<Usuario> existente = obtenerUsuarioPorId(usuario.getId());
        if (existente.isPresent()) {
            Usuario u = existente.get();
            u.setNombre(usuario.getNombre());
            u.setEmail(usuario.getEmail());
            u.setRol(usuario.getRol());
            u.setActivo(usuario.isActivo());
            
            // Solo actualizar contraseña si se proporciona una nueva
            if (usuario.getHashPassword() != null && !usuario.getHashPassword().isEmpty()) {
                u.setHashPassword("hash_" + usuario.getHashPassword());
            }
            
            return u;
        }
        throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuario.getId());
    }
    
    @Override
    public Usuario cambiarEstadoActivacion(Long id, boolean activo) {
        Optional<Usuario> existente = obtenerUsuarioPorId(id);
        if (existente.isPresent()) {
            existente.get().setActivo(activo);
            return existente.get();
        }
        throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
    }
    
    /**
     * Verifica si existe un usuario con el email dado.
     * @param email El email a verificar
     * @return true si existe, false en caso contrario
     */
    private boolean existePorEmail(String email) {
        return usuarios.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
}