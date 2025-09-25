package com.balancerx.infrastructure.importer;

import com.balancerx.application.service.ImportadorDocumentosPort;
import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.domain.valueobject.TipoDocumentoContable;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class DocumentoImportAdapter implements ImportadorDocumentosPort {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<DocumentoContable> importarDesdeExcel(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<DocumentoContable> documentos = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // encabezado
                }
                String numero = row.getCell(0).getStringCellValue();
                LocalDate fecha = row.getCell(1).getLocalDateTimeCellValue().toLocalDate();
                BigDecimal valor = BigDecimal.valueOf(row.getCell(2).getNumericCellValue()).setScale(2);
                documentos.add(crearDocumento(numero, fecha, valor));
            }
            return documentos;
        } catch (IOException e) {
            throw new IllegalStateException("Error procesando Excel", e);
        }
    }

    @Override
    public List<DocumentoContable> importarDesdeCsv(InputStream inputStream) {
        try (CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(inputStream)))) {
            List<DocumentoContable> documentos = new ArrayList<>();
            String[] line;
            boolean header = true;
            while ((line = reader.readNext()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String numero = line[0];
                LocalDate fecha = LocalDate.parse(line[1], DATE_FORMAT);
                BigDecimal valor = new BigDecimal(line[2]).setScale(2);
                documentos.add(crearDocumento(numero, fecha, valor));
            }
            return documentos;
        } catch (IOException e) {
            throw new IllegalStateException("Error procesando CSV", e);
        }
    }

    private DocumentoContable crearDocumento(String numero, LocalDate fecha, BigDecimal valor) {
        return DocumentoContable.builder()
                .id(UUID.randomUUID())
                .tipo(TipoDocumentoContable.FACTURA)
                .numero(numero)
                .fecha(fecha)
                .valor(valor)
                .build();
    }
}
