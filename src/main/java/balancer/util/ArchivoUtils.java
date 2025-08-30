package balancer.util;

import java.io.File;

public final class ArchivoUtils {
    public static final String BASE_DIR = "C:" + File.separator + "Balancer";
    public static final String CONFIG_DIR = BASE_DIR + File.separator + "config";
    public static final String USUARIOS_DIR = BASE_DIR + File.separator + "usuarios";
    public static final String PUNTOS_DIR = BASE_DIR + File.separator + "puntos";
    public static final String CUADRES_DIR = BASE_DIR + File.separator + "cuadres";
    public static final String LOGS_DIR = BASE_DIR + File.separator + "logs";
    private ArchivoUtils(){}
    public static void inicializar(){
        crear(CONFIG_DIR); crear(USUARIOS_DIR); crear(PUNTOS_DIR); crear(CUADRES_DIR); crear(LOGS_DIR);
    }
    private static void crear(String path){
        File f = new File(path);
        if(!f.exists()) f.mkdirs();
    }
}
