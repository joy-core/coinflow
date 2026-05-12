package me.sunlianhui.coinflow.modules.bill.service.impl;

import me.sunlianhui.coinflow.modules.bill.entity.BillEntity;
import me.sunlianhui.coinflow.modules.bill.mapper.BillMapper;
import me.sunlianhui.coinflow.modules.bill.model.BillModel;
import me.sunlianhui.coinflow.modules.bill.service.BillService;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillMapper billMapper;

    @Override
    public List<BillModel> listBills(Map<String, Object> params) {
        List<BillEntity> entities = billMapper.selectAll();
        return entities.stream()
                .filter(entity -> entity.getUserId().equals(params.get("userId")))
                .filter(entity -> {
                    Date happenedAt = entity.getHappenedAt();
                    if (happenedAt == null) return false;
                    Date start = (Date) params.get("startDate");
                    Date end = (Date) params.get("endDate");
                    return (start == null || !happenedAt.before(start))
                        && (end == null || !happenedAt.after(end));
                })
                .map(entity -> {
                    BillModel model = new BillModel();
                    BeanUtils.copyProperties(entity, model);
                    return model;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BillModel add(BillModel model) {
        BillEntity entity = new BillEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setId(new SnowflakeIdGenerator(1, 1).nextId());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        billMapper.insert(entity);
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    @Override
    public BillModel update(BillModel model) {
        BillEntity entity = new BillEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdatedAt(new Date());
        billMapper.update(entity);
        return model;
    }

    @Override
    public void deleteById(Long id) {
        billMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> getStatistics(Map<String, Object> params) {
        // Simple implementation, return empty statistics data
        return Map.of();
    }

    @Override
    public List<BillModel> getRecentBills(Long userId, int limit) {
        return billMapper.selectRecentByUserId(userId, limit).stream()
                .map(this::entityToModel)
                .collect(Collectors.toList());
    }

    private BillModel entityToModel(BillEntity entity) {
        BillModel model = new BillModel();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

}
