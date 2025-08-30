package balancer.repository.impl;

import balancer.repository.*;

public final class RepositoryFactory {
    private RepositoryFactory(){}
    private static final CryptoProvider crypto = new AesGcmCrypto();
    public static UsuarioRepository usuarios(){ return new ArchivoUsuarioRepository(crypto); }
    public static PuntoVentaRepository puntos(){ return new ArchivoPuntoVentaRepository(crypto); }
    public static CuadreRepository cuadres(){ return new ArchivoCuadreRepository(crypto); }
}
