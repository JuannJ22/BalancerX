package balancer.repository.impl;

import balancer.repository.CryptoProvider;
import balancer.util.CryptoUtils;
import balancer.util.KeystoreManager;
import javax.crypto.SecretKey;

public class AesGcmCrypto implements CryptoProvider {
    private final SecretKey key = KeystoreManager.cargarClave();
    @Override public String encrypt(byte[] data){ return CryptoUtils.encrypt(key, data); }
    @Override public byte[] decrypt(String base64){ return CryptoUtils.decrypt(key, base64); }
}
