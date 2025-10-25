package com.balancerx.infrastructure.importer;

import com.balancerx.application.service.ImportadorMovimientosPort;
import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.valueobject.FuenteMovimiento;
import com.balancerx.domain.valueobject.TipoMovimientoBancario;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
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
public class MovimientoImportAdapter implements ImportadorMovimientosPort {
    private static final Logger logger = LoggerFactory.getLogger(MovimientoImportAdapter.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Constantes para índices de columnas
    private static final int COLUMN_FECHA = 0;
    private static final int COLUMN_VALOR = 1;
    private static final int COLUMN_REFERENCIA = 2;
    private static final int MIN_COLUMNS = 3;
    
    // Constantes para mensajes y configuración
    private static final String ERROR_FECHA_INVALIDA = "fecha inválida";
    private static final String ERROR_VALOR_INVALIDO = "valor numérico inválido";
    private static final String ERROR_LINEA_INCOMPLETA = "línea incompleta";
    private static final String ERROR_FILA_INCOMPLETA = "fila incompleta";
    private static final String FORMATO_FECHA_ESPERADO = "yyyy-MM-dd";
    private static final int PROGRESO_LOG_INTERVAL = 100;
    
    // Tipo por defecto configurable
    private final TipoMovimientoBancario tipoMovimientoPorDefecto;
    
    public MovimientoImportAdapter() {
        // Por defecto usar TRANSFERENCIA, pero puede ser configurado
        this.tipoMovimientoPorDefecto = TipoMovimientoBancario.TRANSFERENCIA;
    }
    
    public MovimientoImportAdapter(TipoMovimientoBancario tipoMovimientoPorDefecto) {
        this.tipoMovimientoPorDefecto = tipoMovimientoPorDefecto != null ? 
            tipoMovimientoPorDefecto : TipoMovimientoBancario.TRANSFERENCIA;
    }

    @Override
    public List<MovimientoBancario> importar(FuenteMovimiento fuente, InputStream inputStream) {
        // Determinar el tipo de archivo basado en el contenido o extensión
        // Por ahora, intentamos CSV primero, luego Excel
        try {
            return importarDesdeCsv(inputStream, fuente);
        } catch (Exception csvException) {
            logger.warn("Error procesando como CSV, intentando como Excel: {}", csvException.getMessage());
            try {
                return importarDesdeExcel(inputStream, fuente);
            } catch (Exception excelException) {
                String errorMsg = "Error procesando archivo como CSV y Excel. CSV: " + csvException.getMessage() + 
                                ", Excel: " + excelException.getMessage();
                logger.error("Error procesando archivo para fuente {}: {}", fuente, errorMsg);
                throw new IllegalStateException(errorMsg, excelException);
            }
        }
    }

    public List<MovimientoBancario> importarDesdeCsv(InputStream inputStream, FuenteMovimiento fuente) {
        long startTime = System.currentTimeMillis();
        logger.info("Iniciando importación de movimientos desde CSV para fuente: {}", fuente);
        try {
            List<MovimientoBancario> movimientos = leerCsv(inputStream, fuente);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            logger.info("Importación CSV de movimientos completada: {} movimientos procesados para fuente {} en {} ms", 
                       movimientos.size(), fuente, duration);
            System.out.println("Importación CSV de movimientos completada: " + movimientos.size() + 
                             " movimientos procesados para fuente " + fuente + " en " + duration + " ms");
            return movimientos;
        } catch (CsvValidationException e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            String errorMsg = "Error de validación en archivo CSV de movimientos: " + e.getMessage();
            logger.error("Error de validación CSV para fuente {} después de {} ms: {}", fuente, duration, e.getMessage(), e);
            System.err.println(errorMsg);
            throw new IllegalStateException(errorMsg, e);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            String errorMsg = "Error inesperado procesando CSV de movimientos: " + e.getMessage();
            logger.error("Error inesperado procesando CSV para fuente {} después de {} ms: {}", fuente, duration, e.getMessage(), e);
            System.err.println(errorMsg);
            throw new IllegalStateException(errorMsg, e);
        }
    }

    public List<MovimientoBancario> importarDesdeExcel(InputStream inputStream, FuenteMovimiento fuente) {
        long startTime = System.currentTimeMillis();
        logger.info("Iniciando importación de movimientos desde Excel para fuente: {}", fuente);
        try {
            List<MovimientoBancario> movimientos = leerExcel(inputStream, fuente);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            logger.info("Importación Excel de movimientos completada: {} movimientos procesados para fuente {} en {} ms", 
                       movimientos.size(), fuente, duration);
            System.out.println("Importación Excel de movimientos completada: " + movimientos.size() + 
                             " movimientos procesados para fuente " + fuente + " en " + duration + " ms");
            return movimientos;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            String errorMsg = "Error inesperado procesando Excel de movimientos: " + e.getMessage();
            logger.error("Error inesperado procesando Excel para fuente {} después de {} ms: {}", fuente, duration, e.getMessage(), e);
            System.err.println(errorMsg);
            throw new IllegalStateException(errorMsg, e);
        }
    }

    private List<MovimientoBancario> leerExcel(InputStream inputStream, FuenteMovimiento fuente) throws IOException {
        long startTime = System.currentTimeMillis();
        logger.debug("Iniciando lectura de archivo Excel de movimientos para fuente: {}", fuente);
        
        List<MovimientoBancario> movimientos = new ArrayList<>();
        int rowNumber = 0;
        int errorCount = 0;
        int processedCount = 0;
        
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            logger.debug("Archivo Excel abierto exitosamente");
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                rowNumber++;
                
                // Saltar filas vacías
                if (isEmptyRow(row)) {
                    logger.debug("Saltando fila vacía en posición {}", rowNumber);
                    continue;
                }
                
                // Validar que tenga el número mínimo de celdas
                if (row.getLastCellNum() < MIN_COLUMNS) {
                    errorCount++;
                    String errorMsg = "Fila " + rowNumber + " " + ERROR_FILA_INCOMPLETA + ": se esperaban " + MIN_COLUMNS + 
                                    " columnas, pero se encontraron " + row.getLastCellNum();
                    logger.warn("Fila incompleta en Excel: {}", errorMsg);
                    System.err.println(errorMsg);
                    continue;
                }
                
                try {
                    LocalDate fecha = getCellValueAsDate(row.getCell(COLUMN_FECHA), "fecha");
                    BigDecimal valor = getCellValueAsBigDecimal(row.getCell(COLUMN_VALOR), "valor");
                    String referencia = getCellValueAsString(row.getCell(COLUMN_REFERENCIA), "referencia");
                    
                    MovimientoBancario movimiento = MovimientoBancario.builder()
                         .id(UUID.randomUUID())
                         .tipo(tipoMovimientoPorDefecto)
                         .banco("BANCO_DESCONOCIDO") // Valor por defecto ya que no está en el Excel
                         .fecha(fecha)
                         .valor(valor)
                         .referenciaBanco(referencia)
                         .fuente(fuente)
                         .asignadoPor(null)
                         .puntoVentaId(null)
                         .cuadreId(null)
                         .createdAt(java.time.Instant.now())
                         .version(0L)
                         .build();
                    
                    movimientos.add(movimiento);
                    processedCount++;
                    
                    if (rowNumber % PROGRESO_LOG_INTERVAL == 0) {
                        logger.debug("Procesadas {} filas del Excel, {} registros válidos", rowNumber, processedCount);
                    }
                } catch (NumberFormatException e) {
                    errorCount++;
                    String errorMsg = "Error en fila " + rowNumber + ": " + ERROR_VALOR_INVALIDO + " en celda de valor";
                    logger.warn("Error de valor numérico en Excel: {}", errorMsg);
                    System.err.println(errorMsg);    
                } catch (IllegalArgumentException e) {
                    errorCount++;
                    String errorMsg = "Error en fila " + rowNumber + ": " + e.getMessage();
                    logger.warn("Error de validación en Excel: {}", errorMsg);
                    System.err.println(errorMsg);
                } catch (DateTimeParseException e) {
                    errorCount++;
                    String errorMsg = "Error en fila " + rowNumber + ": " + ERROR_FECHA_INVALIDA + " en celda de fecha. Formato esperado: " + FORMATO_FECHA_ESPERADO;
                    logger.warn("Error de fecha en Excel: {}", errorMsg);
                    System.err.println(errorMsg);
                } catch (Exception e) {
                    errorCount++;
                    String errorMsg = "Error inesperado en fila " + rowNumber + ": " + e.getMessage();
                    logger.error("Error inesperado procesando fila {} del Excel: {}", rowNumber, e.getMessage(), e);
                    System.err.println(errorMsg);
                }
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        logger.info("Lectura Excel completada para fuente {}. Filas procesadas: {}, Registros válidos: {}, Errores: {}, Tiempo: {} ms", 
                   fuente, rowNumber, processedCount, errorCount, duration);
        
        return movimientos;
    }

    private List<MovimientoBancario> leerCsv(InputStream inputStream, FuenteMovimiento fuente) throws IOException, CsvValidationException {
        long startTime = System.currentTimeMillis();
        logger.debug("Iniciando lectura de archivo CSV de movimientos para fuente: {}", fuente);
        
        List<MovimientoBancario> movimientos = new ArrayList<>();
        int lineNumber = 0;
        int errorCount = 0;
        int processedCount = 0;
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] nextLine;
            
            while ((nextLine = reader.readNext()) != null) {
                lineNumber++;
                
                // Saltar líneas vacías
                if (nextLine.length == 0 || (nextLine.length == 1 && nextLine[0].trim().isEmpty())) {
                    logger.debug("Saltando línea vacía en posición {}", lineNumber);
                    continue;
                }
                
                // Validar que tenga el número mínimo de columnas
                if (nextLine.length < MIN_COLUMNS) {
                    errorCount++;
                    String errorMsg = "Línea " + lineNumber + " " + ERROR_LINEA_INCOMPLETA + ": se esperaban " + MIN_COLUMNS + 
                                    " columnas, pero se encontraron " + nextLine.length;
                    logger.warn("Línea incompleta en CSV: {}", errorMsg);
                    System.err.println(errorMsg);
                    continue;
                }
                
                try {
                    LocalDate fecha = LocalDate.parse(nextLine[COLUMN_FECHA].trim(), DATE_FORMAT);
                    BigDecimal valor = new BigDecimal(nextLine[COLUMN_VALOR].trim());
                    String referencia = nextLine[COLUMN_REFERENCIA].trim();
                    
                    MovimientoBancario movimiento = MovimientoBancario.builder()
                         .id(UUID.randomUUID())
                         .tipo(tipoMovimientoPorDefecto)
                         .banco("BANCO_DESCONOCIDO") // Valor por defecto ya que no está en el CSV
                         .fecha(fecha)
                         .valor(valor)
                         .referenciaBanco(referencia)
                         .fuente(fuente)
                         .asignadoPor(null)
                         .puntoVentaId(null)
                         .cuadreId(null)
                         .createdAt(java.time.Instant.now())
                         .version(0L)
                         .build();
                    
                    movimientos.add(movimiento);
                    processedCount++;
                    
                    if (lineNumber % PROGRESO_LOG_INTERVAL == 0) {
                        logger.debug("Procesadas {} líneas del CSV, {} registros válidos", lineNumber, processedCount);
                    }
                    
                } catch (DateTimeParseException e) {
                    errorCount++;
                    String errorMsg = "Error en línea " + lineNumber + ": " + ERROR_FECHA_INVALIDA + " '" + 
                                    nextLine[COLUMN_FECHA] + "'. Formato esperado: " + FORMATO_FECHA_ESPERADO;
                    logger.warn("Error de fecha en CSV: {}", errorMsg);
                    System.err.println(errorMsg);
                } catch (NumberFormatException e) {
                    errorCount++;
                    String errorMsg = "Error en línea " + lineNumber + ": " + ERROR_VALOR_INVALIDO + " '" + 
                                    nextLine[COLUMN_VALOR] + "'";
                    logger.warn("Error de valor numérico en CSV: {}", errorMsg);
                    System.err.println(errorMsg);
                } catch (IllegalArgumentException e) {
                    errorCount++;
                    String errorMsg = "Error en línea " + lineNumber + ": argumento inválido - " + e.getMessage();
                    logger.warn("Error de argumento en CSV: {}", errorMsg);
                    System.err.println(errorMsg);
                } catch (Exception e) {
                    errorCount++;
                    String errorMsg = "Error inesperado en línea " + lineNumber + ": " + e.getMessage();
                    logger.error("Error inesperado procesando línea {} del CSV: {}", lineNumber, e.getMessage(), e);
                    System.err.println(errorMsg);
                }
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        logger.info("Lectura CSV completada para fuente {}. Líneas procesadas: {}, Registros válidos: {}, Errores: {}, Tiempo: {} ms", 
                   fuente, lineNumber, processedCount, errorCount, duration);
        
        return movimientos;
    }
    
    private boolean isEmptyRow(Row row) {
        if (row == null || row.getPhysicalNumberOfCells() == 0) {
            return true;
        }
        
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String cellValue = getCellValueAsString(cell, "cell");
                if (!cellValue.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private String validateAndTrim(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + fieldName + " no puede estar vacío");
        }
        return value.trim();
    }
    
    private String getCellValueAsString(Cell cell, String fieldName) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("El campo " + fieldName + " no puede estar vacío");
        }
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> throw new IllegalArgumentException("Tipo de celda no soportado para " + fieldName);
        };
    }
    
    private LocalDate getCellValueAsDate(Cell cell, String fieldName) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("El campo " + fieldName + " no puede estar vacío");
        }
        
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> cell.getLocalDateTimeCellValue().toLocalDate();
                case STRING -> LocalDate.parse(cell.getStringCellValue().trim(), DATE_FORMAT);
                default -> throw new IllegalArgumentException("Tipo de celda no válido para fecha en " + fieldName);
            };
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido en " + fieldName + ": " + e.getMessage());
        }
    }
    
    private BigDecimal getCellValueAsBigDecimal(Cell cell, String fieldName) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException("El campo " + fieldName + " no puede estar vacío");
        }
        
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2);
                case STRING -> new BigDecimal(cell.getStringCellValue().trim()).setScale(2);
                default -> throw new IllegalArgumentException("Tipo de celda no válido para número en " + fieldName);
            };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato numérico inválido en " + fieldName + ": " + e.getMessage());
        }
    }
}
