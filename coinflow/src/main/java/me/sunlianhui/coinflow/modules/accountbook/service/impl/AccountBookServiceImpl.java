package me.sunlianhui.coinflow.modules.accountbook.service.impl;

import me.sunlianhui.coinflow.common.exception.BizException;
import me.sunlianhui.coinflow.modules.accountbook.entity.AccountBookEntity;
import me.sunlianhui.coinflow.modules.accountbook.mapper.AccountBookMapper;
import me.sunlianhui.coinflow.modules.accountbook.model.AccountBookModel;
import me.sunlianhui.coinflow.modules.accountbook.service.AccountBookService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class AccountBookServiceImpl implements AccountBookService {

    @Autowired
    private AccountBookMapper accountBookMapper;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public List<AccountBookModel> listByUserId(Long userId) {
        List<AccountBookEntity> entities = accountBookMapper.selectByUserId(userId);
        return entities.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public AccountBookModel getById(Long id) {
        AccountBookEntity entity = accountBookMapper.selectById(id);
        if (entity == null) {
            throw new BizException("Account book not found");
        }
        return toModel(entity);
    }

    @Override
    public AccountBookModel add(AccountBookModel model) {
        AccountBookEntity entity = new AccountBookEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(snowflakeIdGenerator.nextId());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());

        List<AccountBookEntity> existing = accountBookMapper.selectByUserId(model.getUserId());
        if (existing.isEmpty()) {
            entity.setIsDefault(true);
        }
        if (Boolean.TRUE.equals(entity.getIsDefault())) {
            clearDefault(model.getUserId());
        }

        accountBookMapper.insert(entity);
        return toModel(entity);
    }

    @Override
    public AccountBookModel update(AccountBookModel model) {
        AccountBookEntity existing = accountBookMapper.selectById(model.getId());
        if (existing == null) {
            throw new BizException("Account book not found");
        }

        AccountBookEntity entity = new AccountBookEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdatedAt(new Date());

        if (Boolean.TRUE.equals(model.getIsDefault()) && !Boolean.TRUE.equals(existing.getIsDefault())) {
            clearDefault(model.getUserId());
        }

        accountBookMapper.update(entity);
        return toModel(entity);
    }

    @Override
    public void deleteById(Long id) {
        AccountBookEntity entity = accountBookMapper.selectById(id);
        if (entity == null) {
            throw new BizException("Account book not found");
        }
        if (Boolean.TRUE.equals(entity.getIsDefault())) {
            throw new BizException("Default account book cannot be deleted");
        }
        accountBookMapper.deleteById(id);
    }

    @Override
    public void setDefault(Long id, Long userId) {
        AccountBookEntity entity = accountBookMapper.selectById(id);
        if (entity == null) {
            throw new BizException("Account book not found");
        }
        if (!entity.getUserId().equals(userId)) {
            throw new BizException("No permission to perform this operation");
        }
        clearDefault(userId);
        entity.setIsDefault(true);
        entity.setUpdatedAt(new Date());
        accountBookMapper.update(entity);
    }

    private void clearDefault(Long userId) {
        List<AccountBookEntity> books = accountBookMapper.selectByUserId(userId);
        for (AccountBookEntity book : books) {
            if (Boolean.TRUE.equals(book.getIsDefault())) {
                book.setIsDefault(false);
                book.setUpdatedAt(new Date());
                accountBookMapper.update(book);
            }
        }
    }

    private AccountBookModel toModel(AccountBookEntity entity) {
        AccountBookModel model = new AccountBookModel();
        BeanUtils.copyProperties(entity, model);
        return model;
    }
}