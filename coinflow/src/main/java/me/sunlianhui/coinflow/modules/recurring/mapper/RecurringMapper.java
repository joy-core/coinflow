package me.sunlianhui.coinflow.modules.recurring.mapper;

import me.sunlianhui.coinflow.modules.recurring.entity.RecurringEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RecurringMapper {

    List<RecurringEntity> selectAll();
    RecurringEntity selectById(Long id);
    void insert(RecurringEntity entity);
    void update(RecurringEntity entity);
    void deleteById(Long id);

    List<RecurringEntity> selectByUserId(@Param("userId") Long userId);

}
