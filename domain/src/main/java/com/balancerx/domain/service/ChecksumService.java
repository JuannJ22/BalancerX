package com.balancerx.domain.service;

public interface ChecksumService {
    String sha256(byte[] content);
}
