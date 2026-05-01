package me.sunlianhui.coinflow.modules.asset.mapper;

import me.sunlianhui.coinflow.modules.asset.entity.AssetAccountEntity;
import java.util.List;

public interface AssetAccountMapper {

    List<AssetAccountEntity> selectAll();
    AssetAccountEntity selectById(Long id);
    void insert(AssetAccountEntity entity);
    void update(AssetAccountEntity entity);
    void deleteById(Long id);

    AssetAccountEntity selectAccountNameById(Long id);

}
