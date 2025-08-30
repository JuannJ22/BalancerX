package balancer.service;

import balancer.model.Usuario;
import balancer.repository.UsuarioRepository;
import balancer.repository.impl.RepositoryFactory;
import balancer.util.Encriptador;
import java.util.List;

public class UsuarioService {
    private final UsuarioRepository repo = RepositoryFactory.usuarios();
    public List<Usuario> listar(){ return repo.listar(); }
    public void agregar(String username, String password, String nombre, String rol){
        Usuario u = Usuario.builder().id(java.util.UUID.randomUUID().toString())
                .username(username).passwordHash(Encriptador.hash(password)).nombre(nombre).rol(rol).build();
        repo.guardar(u);
    }
}
