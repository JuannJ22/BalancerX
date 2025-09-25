package com.balancerx.application.service;

public interface FileStoragePort {
    StoredFile storePdf(String fileName, byte[] content);

    record StoredFile(String path, String checksum) {}
}
