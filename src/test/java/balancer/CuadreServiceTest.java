package balancer;

import balancer.model.Cuadre;
import balancer.service.CuadreService;
import balancer.util.ArchivoUtils;
import balancer.util.KeystoreManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class CuadreServiceTest {
    @BeforeAll static void init(){ ArchivoUtils.inicializar(); KeystoreManager.inicializarSiNoExiste(); }
    @Test void guardarYListar(){
        CuadreService s = new CuadreService();
        Cuadre c = Cuadre.builder().id(UUID.randomUUID().toString()).puntoVentaId("1").fecha(LocalDate.now()).monto(5000).build();
        s.guardar(c);
        assertTrue(s.listarPorPunto("1").stream().anyMatch(q -> c.getId().equals(q.getId())));
    }
}
