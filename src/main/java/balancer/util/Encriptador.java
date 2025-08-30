package balancer.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class Encriptador {
    private static final SecureRandom RAND = new SecureRandom();
    private static final int ITER = 65536;
    private static final int KEYLEN = 256;
    private Encriptador(){}
    public static String hash(String password){
        try{
            byte[] salt = new byte[16];
            RAND.nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITER, KEYLEN);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] res = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(res);
        }catch(Exception e){ throw new RuntimeException(e); }
    }
    public static boolean verifica(String password, String stored){
        try{
            if(stored==null || !stored.contains("$")) return false;
            String[] parts = stored.split("\\$");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITER, KEYLEN);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] test = skf.generateSecret(spec).getEncoded();
            if(test.length!=hash.length) return false;
            int diff=0; for(int i=0;i<test.length;i++) diff |= test[i]^hash[i];
            return diff==0;
        }catch(Exception e){ return false; }
    }
}
