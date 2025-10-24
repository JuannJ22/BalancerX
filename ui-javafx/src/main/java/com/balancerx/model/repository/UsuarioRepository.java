package com.balancerx.model.repository;

import com.balancerx.model.entity.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de usuarios siguiendo el patrón Repository.
 */
public interface UsuarioRepository {
    
    /**
     * Guarda un usuario en el repositorio.
     * @param usuario El usuario a guardar
     * @return El usuario guardado con su ID asignado
     */
    Usuario save(Usuario usuario);
    
    /**
     * Busca un usuario por su ID.
     * @param id El ID del usuario a buscar
     * @return Un Optional que contiene el usuario si existe
     */
    Optional<Usuario> findById(Long id);
    
    /**
     * Busca un usuario por su email.
     * @param email El email del usuario a buscar
     * @return Un Optional que contiene el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Obtiene todos los usuarios del repositorio.
     * @return Lista de todos los usuarios
     */
    List<Usuario> findAll();
    
    /**
     * Elimina un usuario del repositorio.
     * @param id El ID del usuario a eliminar
     */
    void deleteById(Long id);
    
    /**
     * Verifica si existe un usuario con el email proporcionado.
     * @param email El email a verificar
     * @return true si existe un usuario con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);
}