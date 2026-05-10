package me.sunlianhui.coinflow.modules.recurring.scheduler;

import me.sunlianhui.coinflow.modules.bill.entity.BillEntity;
import me.sunlianhui.coinflow.modules.bill.mapper.BillMapper;
import me.sunlianhui.coinflow.modules.recurring.entity.RecurringEntity;
import me.sunlianhui.coinflow.modules.recurring.mapper.RecurringMapper;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class RecurringBillScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecurringBillScheduler.class);

    @Autowired
    private RecurringMapper recurringMapper;

    @Autowired
    private BillMapper billMapper;

    @Scheduled(cron = "0 0 * * * *")
    public void generateRecurringBills() {
        log.info("Recurring bill scheduler started");
        List<RecurringEntity> all = recurringMapper.selectAll();

        Date now = new Date();
        int count = 0;

        for (RecurringEntity recurring : all) {
            if (recurring.getIsActive() == null || !recurring.getIsActive()) continue;
            if (recurring.getStartDate() == null || recurring.getStartDate().after(now)) continue;
            if (recurring.getEndDate() != null && recurring.getEndDate().before(now)) continue;

            String recurringTag = "[Recurring #" + recurring.getId() + "]";
            List<BillEntity> existing = billMapper.selectByRemarkContaining(recurringTag);

            if (!existing.isEmpty()) {
                BillEntity latest = existing.get(0);
                Date nextExpected = calculateNextDate(recurring, latest.getHappenedAt());
                if (nextExpected == null || nextExpected.after(now)) continue;
            }

            BillEntity bill = new BillEntity();
            bill.setId(new SnowflakeIdGenerator(1, 1).nextId());
            bill.setUserId(recurring.getUserId());
            bill.setAccountBookId(recurring.getAccountBookId());
            bill.setAssetAccountId(recurring.getAssetAccountId());
            bill.setCategoryId(recurring.getCategoryId());
            bill.setType(recurring.getType());
            bill.setAmount(recurring.getAmount());
            bill.setRemark(recurringTag + (recurring.getRemark() != null ? recurring.getRemark() : ""));
            bill.setHappenedAt(now);
            bill.setCreatedAt(now);
            bill.setUpdatedAt(now);

            billMapper.insert(bill);
            count++;
            log.info("Generated bill from recurring #{}: amount={}", recurring.getId(), recurring.getAmount());
        }

        log.info("Recurring bill scheduler finished, generated {} bills", count);
    }

    private Date calculateNextDate(RecurringEntity recurring, Date lastDate) {
        if (lastDate == null) {
            return recurring.getStartDate();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastDate);

        switch (recurring.getFrequency()) {
            case "DAILY":
                cal.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case "WEEKLY":
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "MONTHLY":
                cal.add(Calendar.MONTH, 1);
                break;
            case "YEARLY":
                cal.add(Calendar.YEAR, 1);
                break;
            default:
                return null;
        }
        return cal.getTime();
    }

}
