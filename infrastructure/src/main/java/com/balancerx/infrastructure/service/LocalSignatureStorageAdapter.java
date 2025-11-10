package com.balancerx.infrastructure.service;

import com.balancerx.application.service.SignatureStoragePort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalSignatureStorageAdapter implements SignatureStoragePort {
    private static final Logger log = LoggerFactory.getLogger(LocalSignatureStorageAdapter.class);

    @Override
    public byte[] loadSignature(String signaturePath) {
        try {
            Path path = Path.of(signaturePath);
            byte[] bytes = Files.readAllBytes(path);
            if (bytes.length == 0) {
                throw new IllegalStateException("El archivo de firma está vacío");
            }
            return bytes;
        } catch (IOException e) {
            log.error("No se pudo leer la firma electrónica ubicada en {}", signaturePath, e);
            throw new IllegalStateException("No se pudo leer la firma electrónica del usuario", e);
        }
    }
}
