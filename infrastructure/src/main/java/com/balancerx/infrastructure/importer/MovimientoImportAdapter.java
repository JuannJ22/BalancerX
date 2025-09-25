package com.balancerx.infrastructure.importer;

import com.balancerx.application.service.ImportadorMovimientosPort;
import com.balancerx.domain.model.MovimientoBancario;
import com.balancerx.domain.valueobject.FuenteMovimiento;
import com.balancerx.domain.valueobject.TipoMovimientoBancario;
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
public class MovimientoImportAdapter implements ImportadorMovimientosPort {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<MovimientoBancario> importar(FuenteMovimiento fuente, InputStream inputStream) {
        return switch (fuente) {
            case CSV -> leerCsv(inputStream, fuente);
            case EXCEL -> leerExcel(inputStream, fuente);
            case MANUAL -> List.of();
        };
    }

    private List<MovimientoBancario> leerExcel(InputStream inputStream, FuenteMovimiento fuente) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<MovimientoBancario> movimientos = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                LocalDate fecha = row.getCell(0).getLocalDateTimeCellValue().toLocalDate();
                BigDecimal valor = BigDecimal.valueOf(row.getCell(1).getNumericCellValue()).setScale(2);
                String referencia = row.getCell(2).getStringCellValue();
                movimientos.add(crearMovimiento(fuente, fecha, valor, referencia));
            }
            return movimientos;
        } catch (IOException e) {
            throw new IllegalStateException("Error procesando Excel", e);
        }
    }

    private List<MovimientoBancario> leerCsv(InputStream inputStream, FuenteMovimiento fuente) {
        try (CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(inputStream)))) {
            List<MovimientoBancario> movimientos = new ArrayList<>();
            String[] line;
            boolean header = true;
            while ((line = reader.readNext()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                LocalDate fecha = LocalDate.parse(line[0], DATE_FORMAT);
                BigDecimal valor = new BigDecimal(line[1]).setScale(2);
                String referencia = line[2];
                movimientos.add(crearMovimiento(fuente, fecha, valor, referencia));
            }
            return movimientos;
        } catch (IOException e) {
            throw new IllegalStateException("Error procesando CSV", e);
        }
    }

    private MovimientoBancario crearMovimiento(FuenteMovimiento fuente, LocalDate fecha, BigDecimal valor, String referencia) {
        return MovimientoBancario.builder()
                .id(UUID.randomUUID())
                .tipo(TipoMovimientoBancario.TRANSFERENCIA)
                .fecha(fecha)
                .valor(valor)
                .referenciaBanco(referencia)
                .fuente(fuente)
                .build();
    }
}
