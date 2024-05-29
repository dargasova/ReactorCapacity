package excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ExcelWriter {
    public void writeToExcel(Map<String, Map<Integer, Double>> countryData,
                             Map<String, Map<Integer, Double>> operatorData,
                             Map<String, Map<Integer, Double>> regionData) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        createSheet(workbook, countryData, "Страна");
        createSheet(workbook, operatorData, "Оператор");
        createSheet(workbook, regionData, "Регион");

        Path outputDir = Paths.get("output");
        if (Files.notExists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        try (FileOutputStream fileOut = new FileOutputStream("output/consumption_data.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    private void createSheet(Workbook workbook, Map<String, Map<Integer, Double>> data, String sheetName) {
        Sheet sheet = workbook.createSheet(sheetName);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue(sheetName);
        headerRow.createCell(1).setCellValue("Потребление");
        headerRow.createCell(2).setCellValue("Год");

        int rowNum = 1;
        for (Map.Entry<String, Map<Integer, Double>> entry : data.entrySet()) {
            String group = entry.getKey();
            for (Map.Entry<Integer, Double> yearEntry : entry.getValue().entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(group);
                row.createCell(1).setCellValue(yearEntry.getValue());
                row.createCell(2).setCellValue(yearEntry.getKey());
            }
        }
    }
}
