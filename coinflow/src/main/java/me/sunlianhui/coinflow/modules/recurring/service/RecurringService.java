package me.sunlianhui.coinflow.modules.recurring.service;

import me.sunlianhui.coinflow.modules.recurring.model.RecurringModel;
import java.util.List;

public interface RecurringService {

    List<RecurringModel> listByUserId(Long userId);
    RecurringModel add(RecurringModel model);
    RecurringModel update(RecurringModel model);
    void deleteById(Long id);
    void toggleStatus(Long id, Boolean status);

}
