package balancer.repository;

public interface CryptoProvider {
    String encrypt(byte[] data);
    byte[] decrypt(String base64);
}
