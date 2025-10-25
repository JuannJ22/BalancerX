package com.balancerx.infrastructure.importer;

import com.balancerx.application.service.ImportadorDocumentosPort;
import com.balancerx.domain.model.DocumentoContable;
import com.balancerx.domain.valueobject.TipoDocumentoContable;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DocumentoImportAdapter implements ImportadorDocumentosPort {
    private static final Logger logger = LoggerFactory.getLogger(DocumentoImportAdapter.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Constantes para índices de columnas
    private static final int COLUMN_NUMERO = 0;
    private static final int COLUMN_FECHA = 1;
    private static final int COLUMN_VALOR = 2;
    private static final int MIN_COLUMNS = 3;
    
    // Constantes para mensajes y configuración
    private static final String ERROR_FECHA_INVALIDA = "fecha inválida";
    private static final String ERROR_VALOR_INVALIDO = "valor numérico inválido";
    private static final String ERROR_FILA_INCOMPLETA = "fila incompleta";
    private static final String FORMATO_FECHA_ESPERADO = "yyyy-MM-dd";
    private static final int PROGRESO_LOG_INTERVAL = 100;
    
    // Tipo por defecto configurable
    private final TipoDocumentoContable tipoDocumentoPorDefecto;
    
    public DocumentoImportAdapter() {
        // Por defecto usar FACTURA, pero puede ser configurado
        this.tipoDocumentoPorDefecto = TipoDocumentoContable.FACTURA;
    }
    
    public DocumentoImportAdapter(TipoDocumentoContable tipoDocumentoPorDefecto) {
        this.tipoDocumentoPorDefecto = tipoDocumentoPorDefecto != null ? 
            tipoDocumentoPorDefecto : TipoDocumentoContable.FACTURA;
    }

    @Override
    public List<DocumentoContable> importarDesdeExcel(InputStream inputStream) {
        logger.info("Iniciando importación de documentos desde Excel");
        long startTime = System.currentTimeMillis();
        
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<DocumentoContable> documentos = new ArrayList<>();
            int processedRows = 0;
            int totalRows = sheet.getLastRowNum();
            int errorCount = 0;
            
            logger.debug("Archivo Excel abierto exitosamente. Total de filas: {}", totalRows);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // encabezado
                }
                
                // Validar que tenga el número mínimo de celdas
                if (row.getLastCellNum() < MIN_COLUMNS) {
                    errorCount++;
                    String errorMsg = "Fila " + (row.getRowNum() + 1) + " " + ERROR_FILA_INCOMPLETA + ": se esperaban " + MIN_COLUMNS + 
                                    " columnas, pero se encontraron " + row.getLastCellNum();
                    logger.warn("Fila incompleta en Excel: {}", errorMsg);
                    System.err.println(errorMsg);
                    continue;
                }
                
                try {
                    String numero = getCellValueAsString(row.getCell(COLUMN_NUMERO), "número");
                    LocalDate fecha = getCellValueAsDate(row.getCell(COLUMN_FECHA), "fecha");
                    BigDecimal valor = getCellValueAsBigDecimal(row.getCell(COLUMN_VALOR), "valor");
                    
                    DocumentoContable documento = DocumentoContable.builder()
                            .id(UUID.randomUUID())
                            .numero(numero)
                            .tipo(tipoDocumentoPorDefecto)
                            .fecha(fecha)
                            .valor(valor)
                            .build();
                    
                    documentos.add(documento);
                    processedRows++;
                    
                    if (processedRows % PROGRESO_LOG_INTERVAL == 0) {
                        logger.debug("Procesadas {} filas de Excel, {} registros válidos", row.getRowNum(), processedRows);
                    }
                } catch (NumberFormatException e) {
                    errorCount++;
                    logger.warn("Error de formato numérico en fila {}: {}", row.getRowNum() + 1, e.getMessage());
                    System.err.println("Error en fila Excel " + (row.getRowNum() + 1) + ": " + ERROR_VALOR_INVALIDO);
                } catch (IllegalArgumentException e) {
                    errorCount++;
                    logger.warn("Error en fila Excel {}: {}", row.getRowNum() + 1, e.getMessage());
                    System.err.println("Error en fila Excel " + (row.getRowNum() + 1) + ": " + e.getMessage());
                } catch (DateTimeParseException e) {
                    errorCount++;
                    String errorMsg = "Error en fila Excel " + (row.getRowNum() + 1) + ": " + ERROR_FECHA_INVALIDA + " en celda de fecha. Formato esperado: " + FORMATO_FECHA_ESPERADO;
                    logger.warn("Error de formato de fecha en fila Excel {}: {}", row.getRowNum() + 1, e.getMessage());
                    System.err.println(errorMsg);

                } catch (Exception e) {
                    errorCount++;
                    logger.warn("Error inesperado en fila {}: {}", row.getRowNum() + 1, e.getMessage());
                    System.err.println("Error inesperado en fila Excel " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Importación Excel completada en {} ms: {} documentos procesados de {} filas de datos, {} errores", 
                       duration, documentos.size(), totalRows, errorCount);
            System.out.println("Importación Excel completada: " + documentos.size() + " documentos procesados de " + 
                             totalRows + " filas de datos en " + duration + " ms");
            return documentos;
        } catch (IOException e) {
            String errorMsg = "Error leyendo archivo Excel: " + e.getMessage();
            logger.error("Error de E/O procesando archivo Excel: {}", e.getMessage(), e);
            System.err.println(errorMsg);
            throw new IllegalStateException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error inesperado procesando archivo Excel: " + e.getMessage();
            logger.error("Error inesperado procesando archivo Excel: {}", e.getMessage(), e);
            System.err.println(errorMsg);
            throw new IllegalStateException(errorMsg, e);
        }
    }

    @Override
    public List<DocumentoContable> importarDesdeCsv(InputStream inputStream) {
        logger.info("Iniciando importación de documentos desde CSV");
        long startTime = System.currentTimeMillis();
        
        try (CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(inputStream)))) {
            List<DocumentoContable> documentos = new ArrayList<>();
            String[] line;
            boolean header = true;
            int lineNumber = 0;
            int errorCount = 0;
            
            while ((line = reader.readNext()) != null) {
                lineNumber++;
                
                // Validar fila incompleta o vacía
                if (line.length < MIN_COLUMNS || isEmptyRow(line)) {
                    logger.debug("Línea {} omitida: fila vacía o incompleta", lineNumber);
                    System.out.println("Línea " + lineNumber + " omitida: fila vacía o incompleta");
                    continue;
                }
                
                if (header) {
                    header = false;
                    logger.debug("Encabezado CSV procesado en línea {}", lineNumber);
                    continue;
                }
                
                try {
                    String numero = validateAndTrim(line[COLUMN_NUMERO], "número de documento");
                    LocalDate fecha = LocalDate.parse(line[COLUMN_FECHA].trim(), DATE_FORMAT);
                    BigDecimal valor = new BigDecimal(line[COLUMN_VALOR].trim()).setScale(2);
                    documentos.add(crearDocumento(numero, fecha, valor));
                    
                    if (documentos.size() % 100 == 0) {
                        logger.debug("Procesados {} documentos CSV", documentos.size());
                    }
                } catch (DateTimeParseException e) {
                    errorCount++;
                    logger.warn("Error de formato de fecha en línea {}: valor '{}', formato esperado: yyyy-MM-dd", 
                               lineNumber, line[COLUMN_FECHA]);
                    System.err.println("Error en línea " + lineNumber + ": formato de fecha inválido '" + 
                                     line[COLUMN_FECHA] + "'. Formato esperado: yyyy-MM-dd");
                } catch (NumberFormatException e) {
                    errorCount++;
                    logger.warn("Error de formato numérico en línea {}: valor '{}'", lineNumber, line[COLUMN_VALOR]);
                    System.err.println("Error en línea " + lineNumber + ": formato numérico inválido '" + 
                                     line[COLUMN_VALOR] + "' para el valor");
                } catch (IllegalArgumentException e) {
                    errorCount++;
                    logger.warn("Error de validación en línea {}: {}", lineNumber, e.getMessage());
                    System.err.println("Error en línea " + lineNumber + ": " + e.getMessage());
                } catch (Exception e) {
                    errorCount++;
                    logger.error("Error inesperado en línea {}: {}", lineNumber, e.getMessage(), e);
                    System.err.println("Error inesperado en línea " + lineNumber + ": " + e.getMessage());
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Importación CSV completada en {} ms: {} documentos procesados de {} líneas de datos, {} errores", 
                       duration, documentos.size(), lineNumber - 1, errorCount);
            System.out.println("Importación CSV completada: " + documentos.size() + " documentos procesados de " + 
                             (lineNumber - 1) + " líneas de datos");
            return documentos;
        } catch (IOException e) {
            String errorMsg = "Error leyendo archivo CSV: " + e.getMessage();
            logger.error("Error de E/O procesando archivo CSV: {}", e.getMessage(), e);
            System.err.println(errorMsg);
            throw new IllegalStateException(errorMsg, e);
        } catch (CsvValidationException e) {
            String errorMsg = "Error de validación en archivo CSV: " + e.getMessage();
            logger.error("Error de validación CSV: {}", e.getMessage(), e);
            System.err.println(errorMsg);
            throw new IllegalStateException(errorMsg, e);
        }
    }

    private DocumentoContable crearDocumento(String numero, LocalDate fecha, BigDecimal valor) {
        return DocumentoContable.builder()
                .id(UUID.randomUUID())
                .numero(numero)
                .tipo(tipoDocumentoPorDefecto)
                .fecha(fecha)
                .valor(valor)
                .build();
    }
    
    // Métodos de validación y utilidad
    private boolean isEmptyRow(String[] line) {
        for (String cell : line) {
            if (cell != null && !cell.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private String validateAndTrim(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo requerido vacío: " + fieldName);
        }
        return value.trim();
    }
    
    private String getCellValueAsString(Cell cell, String fieldName) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("Campo requerido vacío: " + fieldName);
        }
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> throw new IllegalArgumentException("Tipo de celda no soportado para " + fieldName);
        };
    }
    
    private LocalDate getCellValueAsDate(Cell cell, String fieldName) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("Campo requerido vacío: " + fieldName);
        }
        
        try {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido para " + fieldName + ": " + e.getMessage());
        }
    }
    
    private BigDecimal getCellValueAsBigDecimal(Cell cell, String fieldName) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("Campo requerido vacío: " + fieldName);
        }
        
        try {
            return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato numérico inválido para " + fieldName + ": " + e.getMessage());
        }
    }
}
