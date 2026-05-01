package me.sunlianhui.coinflow.modules.asset.service.impl;

import me.sunlianhui.coinflow.modules.asset.entity.AssetAccountEntity;
import me.sunlianhui.coinflow.modules.asset.mapper.AssetAccountMapper;
import me.sunlianhui.coinflow.modules.asset.model.AssetAccountModel;
import me.sunlianhui.coinflow.modules.asset.service.AssetAccountService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class AssetAccountServiceImpl implements AssetAccountService {

    @Autowired
    private AssetAccountMapper assetAccountMapper;

    @Override
    public List<AssetAccountModel> listByUserId(Long userId) {
        List<AssetAccountEntity> entities = assetAccountMapper.selectAll();
        return entities.stream()
                .filter(entity -> entity.getUserId().equals(userId))
                .map(entity -> {
                    AssetAccountModel model = new AssetAccountModel();
                    BeanUtils.copyProperties(entity, model);
                    return model;
                })
                .collect(Collectors.toList());
    }

    @Override
    public AssetAccountModel add(AssetAccountModel model) {
        AssetAccountEntity entity = new AssetAccountEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        assetAccountMapper.insert(entity);
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    @Override
    public AssetAccountModel update(AssetAccountModel model) {
        AssetAccountEntity entity = new AssetAccountEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdatedAt(new Date());
        assetAccountMapper.update(entity);
        return model;
    }

    @Override
    public void deleteById(Long id) {
        assetAccountMapper.deleteById(id);
    }

}
