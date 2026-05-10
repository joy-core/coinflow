package me.sunlianhui.coinflow.modules.transferrecord.service;

import me.sunlianhui.coinflow.modules.transferrecord.model.TransferRecordModel;
import java.util.List;

public interface TransferRecordService {

    List<TransferRecordModel> listByUserId(Long userId);
    TransferRecordModel add(TransferRecordModel model);
    TransferRecordModel update(TransferRecordModel model);
    void deleteById(Long id);

}
