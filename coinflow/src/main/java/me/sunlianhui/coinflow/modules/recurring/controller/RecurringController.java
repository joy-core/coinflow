package me.sunlianhui.coinflow.modules.recurring.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.recurring.model.RecurringModel;
import me.sunlianhui.coinflow.modules.recurring.service.RecurringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/recurring")
public class RecurringController {

    @Autowired
    private RecurringService recurringService;

    @GetMapping
    public R listRecurring(Long userId) {
        List<RecurringModel> recurringList = recurringService.listByUserId(userId);
        return R.success(recurringList);
    }

    @PostMapping
    public R addRecurring(@RequestBody RecurringModel model) {
        RecurringModel result = recurringService.add(model);
        return R.success(result);
    }

    @PutMapping
    public R updateRecurring(@RequestBody RecurringModel model) {
        RecurringModel result = recurringService.update(model);
        return R.success(result);
    }

    @DeleteMapping("/{id}")
    public R deleteRecurringById(@PathVariable Long id) {
        recurringService.deleteById(id);
        return R.success();
    }

    @PutMapping("/{id}/status")
    public R toggleRecurringStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        recurringService.toggleStatus(id, request.getStatus());
        return R.success();
    }

    public static class StatusRequest {
        private Boolean status;

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }
    }

}
