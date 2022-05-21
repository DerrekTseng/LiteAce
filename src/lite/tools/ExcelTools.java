package lite.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelTools {

	public static byte[] createExcel(List<String> fields, List<Map<String, String>> rows, boolean header) throws IOException {

		byte[] result;

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			
			XSSFSheet sheet = workbook.createSheet();

			if (header) {
				Row row = sheet.createRow(0);
				for (int columnCount = 0; columnCount < fields.size(); columnCount++) {
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(fields.get(columnCount));
				}
			}

			for (int rowCount = 0; rowCount < rows.size(); rowCount++) {
				Row row = sheet.createRow(header ? rowCount + 1 : rowCount);
				Map<String, String> data = rows.get(rowCount);
				for (int columnCount = 0; columnCount < fields.size(); columnCount++) {
					Cell cell = row.createCell(columnCount);
					cell.setCellValue(data.get(fields.get(columnCount)));
				}
			}

			try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				workbook.write(bos);
				result = bos.toByteArray();
			}
		}

		return result;
	}

	public static void createExcel(File file, List<String> fields, List<Map<String, String>> rows, boolean header) throws IOException {
		
		byte[] bytes = createExcel(fields, rows, header);
		
		try (FileOutputStream fos = new FileOutputStream(file, false); BufferedOutputStream bos = new BufferedOutputStream(fos);) {
			bos.write(bytes);
			bos.flush();
		}
		
	}

}
