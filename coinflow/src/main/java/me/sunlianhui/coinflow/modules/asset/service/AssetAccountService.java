package me.sunlianhui.coinflow.modules.asset.service;

import me.sunlianhui.coinflow.modules.asset.model.AssetAccountModel;
import java.util.List;

public interface AssetAccountService {

    List<AssetAccountModel> listByUserId(Long userId);
    AssetAccountModel add(AssetAccountModel model);
    AssetAccountModel update(AssetAccountModel model);
    void deleteById(Long id);

}
