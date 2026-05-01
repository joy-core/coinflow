package me.sunlianhui.coinflow.modules.accountbook.mapper;

import me.sunlianhui.coinflow.modules.accountbook.entity.AccountBookEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AccountBookMapper {

    List<AccountBookEntity> selectByUserId(@Param("userId") Long userId);
    AccountBookEntity selectById(Long id);
    void insert(AccountBookEntity entity);
    void update(AccountBookEntity entity);
    void deleteById(Long id);
}