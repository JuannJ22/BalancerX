package balancer.service;

import balancer.model.Usuario;
import balancer.repository.UsuarioRepository;
import balancer.repository.impl.RepositoryFactory;
import balancer.util.Encriptador;
import java.util.Optional;

public class AuthService {
    private final UsuarioRepository repo = RepositoryFactory.usuarios();
    public AuthService(){
        if(repo.listar().isEmpty()){
            Usuario sa = Usuario.builder()
                    .id("1").nombre("Super Administrador")
                    .username("superadmin")
                    .passwordHash(Encriptador.hash("admin123"))
                    .rol("SUPERADMIN")
                    .build();
            repo.guardar(sa);
        }
    }
    public Usuario login(String username, String password){
        Optional<Usuario> u = repo.buscarPorUsuario(username);
        if(u.isPresent() && Encriptador.verifica(password, u.get().getPasswordHash())) return u.get();
        return null;
    }
    public UsuarioRepository repository(){ return repo; }
}
