package balancer.repository.impl;

import balancer.model.Cuadre;
import balancer.repository.CuadreRepository;
import balancer.repository.CryptoProvider;
import balancer.util.ArchivoUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArchivoCuadreRepository implements CuadreRepository {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final File dir;
    private final CryptoProvider crypto;

    public ArchivoCuadreRepository(CryptoProvider crypto){
        this.crypto = crypto;
        ArchivoUtils.inicializar();
        this.dir = new File(ArchivoUtils.CUADRES_DIR);
        if(!dir.exists()) dir.mkdirs();
    }

    private File fileFor(String puntoId){
        return new File(dir, "cuadres_" + puntoId + ".enc");
    }

    @Override
    public List<Cuadre> listarPorPunto(String puntoId) {
        try{
            File f = fileFor(puntoId);
            if(!f.exists() || f.length()==0) return new ArrayList<>();
            byte[] enc = Files.readAllBytes(f.toPath());
            byte[] json = crypto.decrypt(new String(enc));
            List<Cuadre> list = mapper.readValue(json, new TypeReference<List<Cuadre>>(){});
            list.sort(Comparator.comparing(Cuadre::getFecha).reversed());
            return list;
        }catch(Exception e){ throw new RuntimeException(e); }
    }

    @Override
    public void guardar(Cuadre c) {
        try{
            File f = fileFor(c.getPuntoVentaId());
            List<Cuadre> list = f.exists() && f.length()>0
                    ? mapper.readValue(crypto.decrypt(new String(Files.readAllBytes(f.toPath()))), new TypeReference<List<Cuadre>>(){})
                    : new ArrayList<>();
            boolean replaced=false;
            for(int i=0;i<list.size();i++){
                if(list.get(i).getId()!=null && list.get(i).getId().equals(c.getId())){
                    list.set(i, c); replaced=true; break;
                }
            }
            if(!replaced) list.add(c);
            byte[] json = mapper.writeValueAsBytes(list);
            String out = crypto.encrypt(json);
            Files.writeString(f.toPath(), out);
        }catch(Exception e){ throw new RuntimeException(e); }
    }
}
