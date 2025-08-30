package balancer.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class CryptoUtils {
    private static final int GCM_TAG_LEN = 128;
    private static final int IV_LEN = 12;
    private static final SecureRandom RAND = new SecureRandom();
    private CryptoUtils(){}
    public static String encrypt(SecretKey key, byte[] plaintext){
        try{
            byte[] iv = new byte[IV_LEN];
            RAND.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LEN, iv));
            byte[] c = cipher.doFinal(plaintext);
            byte[] out = new byte[iv.length + c.length];
            System.arraycopy(iv,0,out,0,iv.length);
            System.arraycopy(c,0,out,iv.length,c.length);
            return Base64.getEncoder().encodeToString(out);
        }catch(Exception e){ throw new RuntimeException("Error cifrando", e); }
    }
    public static byte[] decrypt(SecretKey key, String base64){
        try{
            byte[] all = Base64.getDecoder().decode(base64);
            byte[] iv = new byte[IV_LEN];
            System.arraycopy(all,0,iv,0,IV_LEN);
            byte[] cb = new byte[all.length - IV_LEN];
            System.arraycopy(all, IV_LEN, cb, 0, cb.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LEN, iv));
            return cipher.doFinal(cb);
        }catch(Exception e){ throw new RuntimeException("Error descifrando", e); }
    }
}
