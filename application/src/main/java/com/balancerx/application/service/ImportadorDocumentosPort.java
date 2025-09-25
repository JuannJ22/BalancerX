package com.balancerx.application.service;

import com.balancerx.domain.model.DocumentoContable;
import java.io.InputStream;
import java.util.List;

public interface ImportadorDocumentosPort {
    List<DocumentoContable> importarDesdeExcel(InputStream inputStream);

    List<DocumentoContable> importarDesdeCsv(InputStream inputStream);
}
