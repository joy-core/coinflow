package me.sunlianhui.coinflow.modules.transferrecord.mapper;

import me.sunlianhui.coinflow.modules.transferrecord.entity.TransferRecordEntity;
import java.util.List;

public interface TransferRecordMapper {

    List<TransferRecordEntity> selectAll();
    TransferRecordEntity selectById(Long id);
    List<TransferRecordEntity> selectByUserId(Long userId);
    void insert(TransferRecordEntity entity);
    void update(TransferRecordEntity entity);
    void deleteById(Long id);

}
