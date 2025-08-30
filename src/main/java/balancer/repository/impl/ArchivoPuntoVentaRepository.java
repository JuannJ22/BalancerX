package balancer.repository.impl;

import balancer.model.PuntoVenta;
import balancer.repository.CryptoProvider;
import balancer.repository.PuntoVentaRepository;
import balancer.util.ArchivoUtils;
import balancer.util.FileUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArchivoPuntoVentaRepository implements PuntoVentaRepository {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;
    private final CryptoProvider crypto;

    public ArchivoPuntoVentaRepository(CryptoProvider crypto){
        this.crypto = crypto;
        ArchivoUtils.inicializar();
        this.file = new File(ArchivoUtils.PUNTOS_DIR + File.separator + "puntos.enc");
        try{ if(!file.exists()) Files.writeString(file.toPath(), ""); }catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public List<PuntoVenta> listar() {
        try{
            byte[] b = Files.readAllBytes(file.toPath());
            if(b.length==0) return new ArrayList<>();
            byte[] json = crypto.decrypt(new String(b));
            return mapper.readValue(json, new TypeReference<List<PuntoVenta>>(){});
        }catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public Optional<PuntoVenta> buscarPorId(String id) {
        return listar().stream().filter(p -> id.equals(p.getId())).findFirst();
    }

    @Override
    public void guardar(PuntoVenta pv) {
        try{
            List<PuntoVenta> all = listar();
            all.removeIf(x -> x.getId().equals(pv.getId()));
            all.add(pv);
            byte[] json = mapper.writeValueAsBytes(all);
            String enc = crypto.encrypt(json);
            FileUtils.writeBytesAtomic(file.toPath(), enc.getBytes());
        }catch(Exception e){ throw new RuntimeException(e); }
    }
}
