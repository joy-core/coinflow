package me.sunlianhui.coinflow.modules.category.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.category.model.AccountCategoryModel;
import me.sunlianhui.coinflow.modules.category.service.AccountCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RestController
@RequestMapping("/api/categories")
public class AccountCategoryController {

	@Autowired
	private AccountCategoryService categoryService;

	@GetMapping
	public R listCategories(Long userId) {
		return R.success(categoryService.listByUserId(userId));
	}

	@PostMapping
	public R addCategory(@RequestBody AccountCategoryModel model) {
		categoryService.add(model);
		return R.success();
	}

	@PutMapping
	public R updateCategory(@RequestBody AccountCategoryModel model) {
		categoryService.update(model);
		return R.success();
	}

	@DeleteMapping("/{id}")
	public R deleteCategoryById(@PathVariable Long id) {
		categoryService.delete(id);
		return R.success();
	}

	// Export Excel template
	@GetMapping("/export-template")
	public ResponseEntity<byte[]> exportTemplate() throws IOException {
		// Create Excel workbook
		Workbook workbook = new XSSFWorkbook();

		// Create income category sheet
		Sheet incomeSheet = workbook.createSheet("Income Categories");
		// Create expense category sheet
		Sheet expenseSheet = workbook.createSheet("Expense Categories");

		// Define headers
		String[] headers = {"Name", "Parent Category ID", "Icon", "Sort Order"};
		
		// Process income category sheet
		Row incomeHeaderRow = incomeSheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = incomeHeaderRow.createCell(i);
			cell.setCellValue(headers[i]);
		}
		// Income category example data
		Row incomeExampleRow = incomeSheet.createRow(1);
		incomeExampleRow.createCell(0).setCellValue("Salary");
		incomeExampleRow.createCell(1).setCellValue("0");
		incomeExampleRow.createCell(2).setCellValue("salary");
		incomeExampleRow.createCell(3).setCellValue(1);
		
		// Process expense category sheet
		Row expenseHeaderRow = expenseSheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = expenseHeaderRow.createCell(i);
			cell.setCellValue(headers[i]);
		}
		// Expense category example data
		Row expenseExampleRow = expenseSheet.createRow(1);
		expenseExampleRow.createCell(0).setCellValue("Dining");
		expenseExampleRow.createCell(1).setCellValue("0");
		expenseExampleRow.createCell(2).setCellValue("food");
		expenseExampleRow.createCell(3).setCellValue(1);

		// Adjust column widths
		for (int i = 0; i < headers.length; i++) {
			incomeSheet.autoSizeColumn(i);
			expenseSheet.autoSizeColumn(i);
		}

		// Write workbook to output stream
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		byte[] bytes = outputStream.toByteArray();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.setContentDispositionFormData("attachment", "category-template.xlsx");
		httpHeaders.setContentLength(bytes.length);

		return ResponseEntity.ok()
				.headers(httpHeaders)
				.body(bytes);
	}

	// Import categories
	@PostMapping("/import")
	public R importCategories(@RequestParam("file") MultipartFile file, @RequestParam Long userId) throws IOException {
		// Parse Excel file
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		
		// Process income category sheet
		Sheet incomeSheet = workbook.getSheet("Income Categories");
		if (incomeSheet != null) {
			processSheet(incomeSheet, "INCOME", userId);
		}
		
		// Process expense category sheet
		Sheet expenseSheet = workbook.getSheet("Expense Categories");
		if (expenseSheet != null) {
			processSheet(expenseSheet, "EXPENSE", userId);
		}
		
		workbook.close();
		return R.success();
	}
	
	// Process data from a single sheet
	private void processSheet(Sheet sheet, String type, Long userId) {
		// Skip header row, start reading data from the second row
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			
			// Create category model
			AccountCategoryModel model = new AccountCategoryModel();
			model.setUserId(userId);
			model.setType(type);
			
			// Read data
			Cell nameCell = row.getCell(0);
			if (nameCell != null) {
				model.setName(nameCell.getStringCellValue());
			}
			
			Cell pidCell = row.getCell(1);
			if (pidCell != null) {
				model.setPid((long) pidCell.getNumericCellValue());
			}
			
			Cell iconCell = row.getCell(2);
			if (iconCell != null) {
				model.setIcon(iconCell.getStringCellValue());
			}
			
			Cell sortOrderCell = row.getCell(3);
			if (sortOrderCell != null) {
				model.setSortOrder((int) sortOrderCell.getNumericCellValue());
			}
			
			// Save category
			categoryService.add(model);
		}
	}
}
