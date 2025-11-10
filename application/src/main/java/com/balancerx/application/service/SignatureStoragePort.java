package com.balancerx.application.service;

public interface SignatureStoragePort {
    byte[] loadSignature(String signaturePath);
}
