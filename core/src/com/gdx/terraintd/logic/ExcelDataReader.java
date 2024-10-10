package com.gdx.terraintd.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ExcelDataReader {
    public static Map<String, List<Map<String, String>>> readExcelFile(String filePath) {
        Map<String, List<Map<String, String>>> allSheetData = new HashMap<>();

        FileHandle file = Gdx.files.internal(filePath);
        try (InputStream is = file.read();
             Workbook workbook = new XSSFWorkbook(is)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                List<Map<String, String>> sheetData = new ArrayList<>();

                Iterator<Row> rowIterator = sheet.iterator();
                if (rowIterator.hasNext()) {
                    Row headerRow = rowIterator.next();
                    List<String> headers = getRowValuesAsString(headerRow);

                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        Map<String, String> rowData = new LinkedHashMap<>();
                        for (int j = 0; j < headers.size(); j++) {
                            String header = headers.get(j);
                            String value = getCellValueAsString(row.getCell(j));
                            rowData.put(header, value);
                        }
                        sheetData.add(rowData);
                    }
                }

                allSheetData.put(sheetName, sheetData);
            }
        } catch (IOException e) {
            Gdx.app.error("ExcelDataReader", "Error reading Excel file", e);
        }

        return allSheetData;
    }

    private static List<String> getRowValuesAsString(Row row) {
        List<String> values = new ArrayList<>();
        for (Cell cell : row) {
            values.add(getCellValueAsString(cell));
        }
        return values;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}