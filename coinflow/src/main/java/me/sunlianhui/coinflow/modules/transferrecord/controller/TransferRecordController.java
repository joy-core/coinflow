package me.sunlianhui.coinflow.modules.transferrecord.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.transferrecord.model.TransferRecordModel;
import me.sunlianhui.coinflow.modules.transferrecord.service.TransferRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfer-records")
public class TransferRecordController {

    @Autowired
    private TransferRecordService transferRecordService;

    @GetMapping
    public R listTransferRecords(@RequestParam(defaultValue = "1") Long userId) {
        List<TransferRecordModel> records = transferRecordService.listByUserId(userId);
        return R.success(records);
    }

    @PostMapping
    public R addTransferRecord(@RequestBody TransferRecordModel model) {
        return R.success(transferRecordService.add(model));
    }

    @PutMapping
    public R updateTransferRecord(@RequestBody TransferRecordModel model) {
        return R.success(transferRecordService.update(model));
    }

    @DeleteMapping("/{id}")
    public R deleteTransferRecord(@PathVariable Long id) {
        transferRecordService.deleteById(id);
        return R.success();
    }

}
