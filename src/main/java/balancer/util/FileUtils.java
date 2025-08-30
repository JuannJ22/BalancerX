package balancer.util;

import java.io.IOException;
import java.nio.file.*;

public final class FileUtils {
    private FileUtils(){}
    public static void writeBytesAtomic(Path destino, byte[] bytes) throws IOException {
        Path temp = Files.createTempFile(destino.getParent(), "tmp-", ".tmp");
        try{
            Files.write(temp, bytes, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(temp, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }finally{ try{ Files.deleteIfExists(temp); }catch(Exception ignore){} }
    }
}
