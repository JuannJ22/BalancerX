package balancer;

import balancer.model.Usuario;
import balancer.service.AuthService;
import balancer.util.ArchivoUtils;
import balancer.util.KeystoreManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    @BeforeAll static void init(){ ArchivoUtils.inicializar(); KeystoreManager.inicializarSiNoExiste(); }
    @Test void loginOk(){
        AuthService s = new AuthService();
        Usuario u = s.login("superadmin","admin123");
        assertNotNull(u);
        assertEquals("SUPERADMIN", u.getRol());
    }
    @Test void loginFail(){
        AuthService s = new AuthService();
        assertNull(s.login("no","existe"));
    }
}
