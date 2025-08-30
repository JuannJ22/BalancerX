package balancer;

import balancer.service.PuntoVentaService;
import balancer.util.ArchivoUtils;
import balancer.util.KeystoreManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PuntoVentaServiceTest {
    @BeforeAll static void init(){ ArchivoUtils.inicializar(); KeystoreManager.inicializarSiNoExiste(); }
    @Test void listaPuntos(){
        var s = new PuntoVentaService();
        assertTrue(s.listar().size() >= 4);
    }
}
