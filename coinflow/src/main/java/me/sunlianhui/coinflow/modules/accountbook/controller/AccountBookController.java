package me.sunlianhui.coinflow.modules.accountbook.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.accountbook.model.AccountBookModel;
import me.sunlianhui.coinflow.modules.accountbook.service.AccountBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account-books")
public class AccountBookController {

    @Autowired
    private AccountBookService accountBookService;

    @GetMapping
    public R listAccountBooks(Long userId) {
        List<AccountBookModel> books = accountBookService.listByUserId(userId);
        return R.success(books);
    }

    @GetMapping("/{id}")
    public R getAccountBook(@PathVariable Long id) {
        AccountBookModel book = accountBookService.getById(id);
        return R.success(book);
    }

    @PostMapping
    public R addAccountBook(@RequestBody AccountBookModel model) {
        AccountBookModel result = accountBookService.add(model);
        return R.success(result);
    }

    @PutMapping
    public R updateAccountBook(@RequestBody AccountBookModel model) {
        AccountBookModel result = accountBookService.update(model);
        return R.success(result);
    }

    @DeleteMapping("/{id}")
    public R deleteAccountBook(@PathVariable Long id) {
        accountBookService.deleteById(id);
        return R.success();
    }

    @PutMapping("/{id}/default")
    public R setDefault(@PathVariable Long id, Long userId) {
        accountBookService.setDefault(id, userId);
        return R.success();
    }
}