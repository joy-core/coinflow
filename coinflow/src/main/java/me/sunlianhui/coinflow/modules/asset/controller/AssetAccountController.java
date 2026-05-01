package me.sunlianhui.coinflow.modules.asset.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.asset.model.AssetAccountModel;
import me.sunlianhui.coinflow.modules.asset.service.AssetAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AssetAccountController {

    @Autowired
    private AssetAccountService assetAccountService;

    @GetMapping
    public R listAccounts(Long userId) {
        List<AssetAccountModel> accounts = assetAccountService.listByUserId(userId);
        return R.success(accounts);
    }

    @PostMapping
    public R addAccount(@RequestBody AssetAccountModel model) {
        AssetAccountModel result = assetAccountService.add(model);
        return R.success(result);
    }

    @PutMapping
    public R updateAccount(@RequestBody AssetAccountModel model) {
        AssetAccountModel result = assetAccountService.update(model);
        return R.success(result);
    }

    @DeleteMapping("/{id}")
    public R deleteAccountById(@PathVariable Long id) {
        assetAccountService.deleteById(id);
        return R.success();
    }

}
