package me.sunlianhui.coinflow.modules.template.service.impl;

import me.sunlianhui.coinflow.modules.template.entity.TemplateEntity;
import me.sunlianhui.coinflow.modules.template.mapper.TemplateMapper;
import me.sunlianhui.coinflow.modules.template.model.TemplateModel;
import me.sunlianhui.coinflow.modules.template.service.TemplateService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Override
    public List<TemplateModel> listByUserId(Long userId) {
        List<TemplateEntity> entities = templateMapper.selectAll();
        return entities.stream()
                .filter(entity -> entity.getUserId().equals(userId))
                .map(entity -> {
                    TemplateModel model = new TemplateModel();
                    BeanUtils.copyProperties(entity, model);
                    return model;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TemplateModel add(TemplateModel model) {
        TemplateEntity entity = new TemplateEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        templateMapper.insert(entity);
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    @Override
    public TemplateModel update(TemplateModel model) {
        TemplateEntity entity = new TemplateEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdatedAt(new Date());
        templateMapper.update(entity);
        return model;
    }

    @Override
    public void deleteById(Long id) {
        templateMapper.deleteById(id);
    }

    @Override
    public void useTemplate(Long id) {
        // Simple implementation, should actually create bills from template
    }

}
