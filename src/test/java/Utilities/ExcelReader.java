package Utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public static List<Object[]> readExcelData(String relativePathInResources, String sheetName) throws Exception {
        List<Object[]> data = new ArrayList<>();
        ClassLoader classLoader = ExcelReader.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(relativePathInResources)) {
            if (inputStream == null) {
                throw new Exception("Không tìm thấy file: " + relativePathInResources);
            }

            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new Exception("Không tìm thấy sheet '" + sheetName + "'");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int numOfCells = row.getPhysicalNumberOfCells();
                Object[] rowData = new Object[numOfCells];

                for (int j = 0; j < numOfCells; j++) {
                    rowData[j] = getCellValue(row.getCell(j));
                }

                data.add(rowData);
            }
        }

        return data;
    }

    // Hàm đọc giá trị từ một ô Excel
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double val = cell.getNumericCellValue();
                    return (val == (int) val) ? String.valueOf((int) val) : String.valueOf(val);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
