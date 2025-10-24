package com.balancerx.model.service;

import com.balancerx.model.entity.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de usuarios siguiendo el patrón Service.
 */
public interface UsuarioService {
    
    /**
     * Registra un nuevo usuario en el sistema.
     * @param usuario El usuario a registrar
     * @return El usuario registrado
     */
    Usuario registrarUsuario(Usuario usuario);
    
    /**
     * Autentica un usuario en el sistema.
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Optional con el usuario si la autenticación es exitosa
     */
    Optional<Usuario> autenticarUsuario(String email, String password);
    
    /**
     * Obtiene un usuario por su ID.
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    
    /**
     * Obtiene todos los usuarios del sistema.
     * @return Lista de usuarios
     */
    List<Usuario> obtenerTodosLosUsuarios();
    
    /**
     * Actualiza la información de un usuario.
     * @param usuario Usuario con la información actualizada
     * @return El usuario actualizado
     */
    Usuario actualizarUsuario(Usuario usuario);
    
    /**
     * Cambia el estado de activación de un usuario.
     * @param id ID del usuario
     * @param activo Nuevo estado de activación
     * @return El usuario actualizado
     */
    Usuario cambiarEstadoActivacion(Long id, boolean activo);
}