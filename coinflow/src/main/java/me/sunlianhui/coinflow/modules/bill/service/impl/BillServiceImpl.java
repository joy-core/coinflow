package me.sunlianhui.coinflow.modules.bill.service.impl;

import me.sunlianhui.coinflow.modules.bill.entity.BillEntity;
import me.sunlianhui.coinflow.modules.bill.mapper.BillMapper;
import me.sunlianhui.coinflow.modules.bill.model.BillModel;
import me.sunlianhui.coinflow.modules.bill.service.BillService;
import me.sunlianhui.coinflow.modules.asset.mapper.AssetAccountMapper;
import me.sunlianhui.coinflow.modules.category.mapper.AccountCategoryMapper;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private AssetAccountMapper assetAccountMapper;

    @Autowired
    private AccountCategoryMapper categoryMapper;

    @Override
    public List<BillModel> listBills(Map<String, Object> params) {
        List<BillEntity> entities = billMapper.selectAll();
        return entities.stream()
                .filter(entity -> entity.getUserId().equals(params.get("userId")))
                .filter(entity -> {
                    Date happenedAt = entity.getHappenedAt();
                    if (happenedAt == null) return false;
                    Date start = (Date) params.get("startDate");
                    Date end = (Date) params.get("endDate");
                    return (start == null || !happenedAt.before(start))
                        && (end == null || !happenedAt.after(end));
                })
                .map(entity -> {
                    BillModel model = new BillModel();
                    BeanUtils.copyProperties(entity, model);
                    return model;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BillModel add(BillModel model) {
        BillEntity entity = new BillEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        billMapper.insert(entity);
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    @Override
    public BillModel update(BillModel model) {
        BillEntity entity = new BillEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdatedAt(new Date());
        billMapper.update(entity);
        return model;
    }

    @Override
    public void deleteById(Long id) {
        billMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> getStatistics(Map<String, Object> params) {
        // Simple implementation, return empty statistics data
        return Map.of();
    }

    @Override
    public List<BillModel> getRecentBills(Long userId, int limit) {
        return billMapper.selectRecentByUserId(userId, limit).stream()
                .map(this::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillModel> listBillsForExport(Long userId, Date startDate, Date endDate) {
        List<BillEntity> entities = billMapper.selectByUserIdWithFilters(userId, startDate, endDate);
        return entities.stream()
                .map(entity -> {
                    BillModel model = entityToModel(entity);
                    if (entity.getCategoryId() != null) {
                        var cat = categoryMapper.selectById(entity.getCategoryId());
                        if (cat != null) {
                            model.setCategoryName(cat.getName());
                        }
                    }
                    if (entity.getAssetAccountId() != null) {
                        var acc = assetAccountMapper.selectById(entity.getAssetAccountId());
                        if (acc != null) {
                            model.setAccountName(acc.getName());
                        }
                    }
                    return model;
                })
                .collect(Collectors.toList());
    }

    @Override
    public byte[] exportToExcel(List<BillModel> bills) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Bills");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Date", "Type", "Category", "Amount", "Account", "Description", "Location"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < bills.size(); i++) {
            BillModel bill = bills.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(bill.getHappenedAt() != null ? sdf.format(bill.getHappenedAt()) : "");
            row.createCell(1).setCellValue(bill.getType() != null ? bill.getType() : "");
            row.createCell(2).setCellValue(bill.getCategoryName() != null ? bill.getCategoryName() : "");
            row.createCell(3).setCellValue(bill.getAmount() != null ? bill.getAmount().doubleValue() : 0);
            row.createCell(4).setCellValue(bill.getAccountName() != null ? bill.getAccountName() : "");
            row.createCell(5).setCellValue(bill.getRemark() != null ? bill.getRemark() : "");
            row.createCell(6).setCellValue(bill.getLocation() != null ? bill.getLocation() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            wb.write(out);
            wb.close();
            return out.toByteArray();
        }
    }

    @Override
    public byte[] exportToCsv(List<BillModel> bills) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        sb.append("Date,Type,Category,Amount,Account,Description,Location\n");
        for (BillModel bill : bills) {
            sb.append(bill.getHappenedAt() != null ? sdf.format(bill.getHappenedAt()) : "").append(",");
            sb.append(bill.getType() != null ? bill.getType() : "").append(",");
            sb.append(csvEscape(bill.getCategoryName())).append(",");
            sb.append(bill.getAmount() != null ? bill.getAmount().doubleValue() : 0).append(",");
            sb.append(csvEscape(bill.getAccountName())).append(",");
            sb.append(csvEscape(bill.getRemark())).append(",");
            sb.append(csvEscape(bill.getLocation())).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String csvEscape(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private BillModel entityToModel(BillEntity entity) {
        BillModel model = new BillModel();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

}
