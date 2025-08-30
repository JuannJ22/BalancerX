package balancer.util;

import java.io.File;
import java.nio.file.Path;

public final class ArchivoUtils {
    public static final String BASE_DIR =
            Path.of(System.getProperty("user.home"), "Balancer").toString();
    public static final String CONFIG_DIR = Path.of(BASE_DIR, "config").toString();
    public static final String USUARIOS_DIR = Path.of(BASE_DIR, "usuarios").toString();
    public static final String PUNTOS_DIR = Path.of(BASE_DIR, "puntos").toString();
    public static final String CUADRES_DIR = Path.of(BASE_DIR, "cuadres").toString();
    public static final String LOGS_DIR = Path.of(BASE_DIR, "logs").toString();
    private ArchivoUtils(){}
    public static void inicializar(){
        crear(CONFIG_DIR); crear(USUARIOS_DIR); crear(PUNTOS_DIR); crear(CUADRES_DIR); crear(LOGS_DIR);
    }
    private static void crear(String path){
        File f = new File(path);
        if(!f.exists()) f.mkdirs();
    }
}
