package me.sunlianhui.coinflow.modules.accountbook.service;

import me.sunlianhui.coinflow.modules.accountbook.model.AccountBookModel;
import java.util.List;

public interface AccountBookService {

    List<AccountBookModel> listByUserId(Long userId);
    AccountBookModel getById(Long id);
    AccountBookModel add(AccountBookModel model);
    AccountBookModel update(AccountBookModel model);
    void deleteById(Long id);
    void setDefault(Long id, Long userId);
}