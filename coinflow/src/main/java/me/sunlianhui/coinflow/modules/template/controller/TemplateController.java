package me.sunlianhui.coinflow.modules.template.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.template.model.TemplateModel;
import me.sunlianhui.coinflow.modules.template.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping
    public R listTemplates(Long userId) {
        List<TemplateModel> templates = templateService.listByUserId(userId);
        return R.success(templates);
    }

    @PostMapping
    public R addTemplate(@RequestBody TemplateModel model) {
        TemplateModel result = templateService.add(model);
        return R.success(result);
    }

    @PutMapping
    public R updateTemplate(@RequestBody TemplateModel model) {
        TemplateModel result = templateService.update(model);
        return R.success(result);
    }

    @DeleteMapping("/{id}")
    public R deleteTemplateById(@PathVariable Long id) {
        templateService.deleteById(id);
        return R.success();
    }

    @PostMapping("/{id}/use")
    public R useTemplate(@PathVariable Long id) {
        templateService.useTemplate(id);
        return R.success();
    }

}
