package me.sunlianhui.coinflow.modules.template.service;

import me.sunlianhui.coinflow.modules.template.model.TemplateModel;
import java.util.List;

public interface TemplateService {

    List<TemplateModel> listByUserId(Long userId);
    TemplateModel add(TemplateModel model);
    TemplateModel update(TemplateModel model);
    void deleteById(Long id);
    void useTemplate(Long id);

}
