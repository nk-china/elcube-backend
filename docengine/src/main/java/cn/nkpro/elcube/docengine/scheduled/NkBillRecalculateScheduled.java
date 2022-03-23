package cn.nkpro.elcube.docengine.scheduled;

import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.security.NkSecurityRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@ConditionalOnProperty("nk.doc.cron.bill-recalculate")
@Slf4j
@Component
public class NkBillRecalculateScheduled {

    private static final String LOCK_ID = "NK_BILL_RECALCULATE:";
    private static final String CACHE_KEY = "KEEP:NK_BILL_RECALCULATE:";

    @Autowired@SuppressWarnings("all")
    private JdbcTemplate jdbcTemplate;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<String> redisSupport;

    @Autowired@SuppressWarnings("all")
    private NkDocEngine docEngine;

    @Autowired@SuppressWarnings("all")
    private NkSecurityRunner runner;

    @Scheduled(cron = "${nk.doc.cron.bill-recalculate}")
    public void exec(){

        // 获取逾期的账单ID

        List<String> docIds = jdbcTemplate.query(
                "select distinct DOC_ID " +
                        "  from NK_DOC_I_BILL " +
                        " where DISCARD = 0 " +
                        "   and STATE = 1 " +
                        "   and RECEIVABLE >= 0.004 " +
                        "   and EXPIRE_DATE <= ?",
                (resultSet, i) -> resultSet.getString(1),
                today()-1
        );

        runner.runAsUser("admin",  ()->
            docIds.forEach( docId ->{

                // 当前日期已计算的账单ID缓存，避免重复计算
                // 每次都重新计算日期是为了避免程序运行时跨天
                String existsKey = CACHE_KEY+today();

                // 跳过已经执行的单据
                if(null!=redisSupport.get(existsKey,docId)){
                   return;
                }

                // 尝试锁定单据，避免多个服务对同一个单据进行处理
                String lock = redisSupport.lock(LOCK_ID+docId,3600);
                try{
                    docEngine.doUpdate(docId,"重计滞纳金",  d ->
                        docEngine.calculate(d,null,null)
                    );
                    // 设置单据成功标识，避免重复计算
                    redisSupport.set(existsKey,docId,docId);
                    redisSupport.expire(existsKey,60*60*24);
                }catch(Exception e){
                    log.error("重计滞纳金发生错误：docId="+docId,e);
                }finally{
                    // 不论是否成功，解锁单据
                    redisSupport.unlock(LOCK_ID+docId,lock);
                }
            })
        );
    }

    private static long today(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }
}
