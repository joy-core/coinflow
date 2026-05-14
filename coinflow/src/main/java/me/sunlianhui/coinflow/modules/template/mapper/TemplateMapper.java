package me.sunlianhui.coinflow.modules.template.mapper;

import me.sunlianhui.coinflow.modules.template.entity.TemplateEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TemplateMapper {

    List<TemplateEntity> selectAll();
    TemplateEntity selectById(Long id);
    void insert(TemplateEntity entity);
    void update(TemplateEntity entity);
    void deleteById(Long id);

    List<TemplateEntity> selectByUserId(@Param("userId") Long userId);

}
