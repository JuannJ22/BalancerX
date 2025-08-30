package balancer.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

public final class KeystoreManager {
    public static final String KEYSTORE_PATH = ArchivoUtils.CONFIG_DIR + File.separator + "keystore.jks";
    private static final String KEY_ALIAS = "balancer_aes";
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static final int AES_BITS = 256;
    private KeystoreManager(){}
    public static void inicializarSiNoExiste(){
        try{
            File kf = new File(KEYSTORE_PATH);
            String pwd = System.getenv().getOrDefault("BALANCER_KEYSTORE_PASS","balancerpass");
            char[] pass = pwd.toCharArray();
            File dir = kf.getParentFile();
            if(!dir.exists()) dir.mkdirs();
            KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
            if(!kf.exists()){
                ks.load(null, pass);
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(AES_BITS, new SecureRandom());
                SecretKey key = kg.generateKey();
                KeyStore.SecretKeyEntry sk = new KeyStore.SecretKeyEntry(key);
                KeyStore.ProtectionParameter prot = new KeyStore.PasswordProtection(pass);
                ks.setEntry(KEY_ALIAS, sk, prot);
                try(FileOutputStream fos = new FileOutputStream(kf)){ ks.store(fos, pass); }
            }
        }catch(Exception e){ throw new RuntimeException("Error keystore", e); }
    }
    public static javax.crypto.SecretKey cargarClave(){
        try{
            File kf = new File(KEYSTORE_PATH);
            String pwd = System.getenv().getOrDefault("BALANCER_KEYSTORE_PASS","balancerpass");
            char[] pass = pwd.toCharArray();
            KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
            try(java.io.FileInputStream fis = new java.io.FileInputStream(kf)){ ks.load(fis, pass); }
            KeyStore.ProtectionParameter prot = new KeyStore.PasswordProtection(pass);
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) ks.getEntry("balancer_aes", prot);
            return entry.getSecretKey();
        }catch(Exception e){ throw new RuntimeException("No se pudo cargar clave", e); }
    }
}
