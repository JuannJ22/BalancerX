package com.balancerx.controller;

import com.balancerx.model.entity.Usuario;
import com.balancerx.model.service.UsuarioService;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de usuarios.
 * Actúa como puente entre el modelo y el viewController.
 */
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    /**
     * Constructor que inyecta el servicio de usuarios.
     * @param usuarioService Servicio de usuarios
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    /**
     * Autentica un usuario en el sistema.
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Optional con el usuario si la autenticación es exitosa
     */
    public Optional<Usuario> autenticarUsuario(String email, String password) {
        return usuarioService.autenticarUsuario(email, password);
    }
    
    /**
     * Registra un nuevo usuario en el sistema.
     * @param nombre Nombre del usuario
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param rol Rol del usuario
     * @return El usuario registrado
     */
    public Usuario registrarUsuario(String nombre, String email, String password, String rol) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setRol(rol);
        // En un caso real, aquí se haría el hash de la contraseña
        nuevoUsuario.setHashPassword(password);
        nuevoUsuario.setActivo(true);
        
        return usuarioService.registrarUsuario(nuevoUsuario);
    }
    
    /**
     * Obtiene todos los usuarios del sistema.
     * @return Lista de usuarios
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
    }
    
    /**
     * Actualiza la información de un usuario.
     * @param id ID del usuario
     * @param nombre Nuevo nombre
     * @param email Nuevo email
     * @param rol Nuevo rol
     * @return El usuario actualizado
     */
    public Optional<Usuario> actualizarUsuario(Long id, String nombre, String email, String rol) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorId(id);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setRol(rol);
            
            return Optional.of(usuarioService.actualizarUsuario(usuario));
        }
        
        return Optional.empty();
    }
    
    /**
     * Cambia el estado de activación de un usuario.
     * @param id ID del usuario
     * @param activo Nuevo estado de activación
     * @return El usuario actualizado
     */
    public Optional<Usuario> cambiarEstadoActivacion(Long id, boolean activo) {
        try {
            Usuario usuario = usuarioService.cambiarEstadoActivacion(id, activo);
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}