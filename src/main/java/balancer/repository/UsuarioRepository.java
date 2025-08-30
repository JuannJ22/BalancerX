package balancer.repository;

import balancer.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    List<Usuario> listar();
    Optional<Usuario> buscarPorUsuario(String username);
    void guardar(Usuario u);
}
