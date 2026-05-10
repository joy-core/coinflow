package me.sunlianhui.coinflow.modules.transferrecord.service.impl;

import me.sunlianhui.coinflow.modules.transferrecord.entity.TransferRecordEntity;
import me.sunlianhui.coinflow.modules.transferrecord.mapper.TransferRecordMapper;
import me.sunlianhui.coinflow.modules.transferrecord.model.TransferRecordModel;
import me.sunlianhui.coinflow.modules.transferrecord.service.TransferRecordService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferRecordServiceImpl implements TransferRecordService {

    @Autowired
    private TransferRecordMapper transferRecordMapper;

    @Override
    public List<TransferRecordModel> listByUserId(Long userId) {
        List<TransferRecordEntity> entities = transferRecordMapper.selectByUserId(userId);
        return entities.stream()
                .map(this::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public TransferRecordModel add(TransferRecordModel model) {
        TransferRecordEntity entity = new TransferRecordEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
        entity.setCreatedAt(new Date());
        if (entity.getTransferredAt() == null) {
            entity.setTransferredAt(new Date());
        }
        transferRecordMapper.insert(entity);
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    @Override
    public TransferRecordModel update(TransferRecordModel model) {
        TransferRecordEntity entity = new TransferRecordEntity();
        BeanUtils.copyProperties(model, entity);
        transferRecordMapper.update(entity);
        return model;
    }

    @Override
    public void deleteById(Long id) {
        transferRecordMapper.deleteById(id);
    }

    private TransferRecordModel entityToModel(TransferRecordEntity entity) {
        TransferRecordModel model = new TransferRecordModel();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

}
