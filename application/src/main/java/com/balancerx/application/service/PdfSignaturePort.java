package com.balancerx.application.service;

public interface PdfSignaturePort {
    byte[] applySignature(byte[] pdfContent, byte[] signatureContent);
}
