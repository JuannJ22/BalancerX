package balancer.repository.impl;

import balancer.model.Usuario;
import balancer.repository.CryptoProvider;
import balancer.repository.UsuarioRepository;
import balancer.util.ArchivoUtils;
import balancer.util.FileUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArchivoUsuarioRepository implements UsuarioRepository {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;
    private final CryptoProvider crypto;

    public ArchivoUsuarioRepository(CryptoProvider crypto){
        this.crypto = crypto;
        ArchivoUtils.inicializar();
        this.file = new File(ArchivoUtils.USUARIOS_DIR + File.separator + "usuarios.enc");
        try{ if(!file.exists()) Files.writeString(file.toPath(), ""); }catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public List<Usuario> listar() {
        try{
            byte[] b = Files.readAllBytes(file.toPath());
            if(b.length==0) return new ArrayList<>();
            byte[] json = crypto.decrypt(new String(b));
            return mapper.readValue(json, new TypeReference<List<Usuario>>(){});
        }catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public Optional<Usuario> buscarPorUsuario(String username) {
        return listar().stream().filter(u -> username.equals(u.getUsername())).findFirst();
    }

    @Override
    public void guardar(Usuario u) {
        try{
            List<Usuario> all = listar();
            all.removeIf(x -> x.getUsername().equals(u.getUsername()));
            all.add(u);
            byte[] json = mapper.writeValueAsBytes(all);
            String enc = crypto.encrypt(json);
            FileUtils.writeBytesAtomic(file.toPath(), enc.getBytes());
        }catch(Exception e){ throw new RuntimeException(e); }
    }
}
