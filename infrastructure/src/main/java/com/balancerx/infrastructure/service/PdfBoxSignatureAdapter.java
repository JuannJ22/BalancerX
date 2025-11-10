package com.balancerx.infrastructure.service;

import com.balancerx.application.service.PdfSignaturePort;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.springframework.stereotype.Component;

@Component
public class PdfBoxSignatureAdapter implements PdfSignaturePort {
    private static final float SIGNATURE_MAX_WIDTH = 180f;
    private static final float SIGNATURE_MAX_HEIGHT = 90f;
    private static final float MARGIN = 36f;

    @Override
    public byte[] applySignature(byte[] pdfContent, byte[] signatureContent) {
        try (PDDocument document = PDDocument.load(pdfContent);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (document.getNumberOfPages() == 0) {
                throw new IllegalArgumentException("El PDF no contiene páginas para firmar");
            }
            PDPage page = document.getPage(0);
            PDImageXObject signatureImage = PDImageXObject.createFromByteArray(document, signatureContent, "firma");
            PDRectangle mediaBox = page.getMediaBox();

            float imageWidth = signatureImage.getWidth();
            float imageHeight = signatureImage.getHeight();
            float widthScale = SIGNATURE_MAX_WIDTH / imageWidth;
            float heightScale = SIGNATURE_MAX_HEIGHT / imageHeight;
            float scale = Math.min(Math.min(widthScale, heightScale), 1.0f);
            float drawWidth = imageWidth * scale;
            float drawHeight = imageHeight * scale;

            float x = mediaBox.getWidth() - drawWidth - MARGIN;
            float y = MARGIN;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
                contentStream.drawImage(signatureImage, x, y, drawWidth, drawHeight);
            }

            document.save(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo aplicar la firma electrónica al PDF", e);
        }
    }
}
