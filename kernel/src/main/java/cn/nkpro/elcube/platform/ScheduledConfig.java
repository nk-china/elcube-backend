/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.platform;

import cn.nkpro.elcube.platform.model.NkHeartbeatEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class ScheduledConfig implements SchedulingConfigurer {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(cron = "0 * * * * ?")
    public void heartbeat(){
        log.debug("heartbeat");
        applicationEventPublisher.publishEvent(new NkHeartbeatEvent(System.currentTimeMillis()));
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

//        System.out.println(123);
//
//        Runnable task = new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("run");
//            }
//        };
//
//        taskRegistrar.addTriggerTask(task,
//                triggerContext -> {
//                    System.out.println("retime");
//                    return new CronTrigger("0/10 * * * * ?").nextExecutionTime(triggerContext);
//                });
//        for (SpringScheduledCron springScheduledCron : cronRepository.findAll()) {
//            Class<?> clazz;
//            Object task;
//            try {
//                clazz = Class.forName(springScheduledCron.getCronKey());
//                task = context.getBean(clazz);
//            } catch (ClassNotFoundException e) {
//                throw new IllegalArgumentException("spring_scheduled_cron表数据" + springScheduledCron.getCronKey() + "有误", e);
//            } catch (BeansException e) {
//                throw new IllegalArgumentException(springScheduledCron.getCronKey() + "未纳入到spring管理", e);
//            }
//            Assert.isAssignable(ScheduledOfTask.class, task.getClass(), "定时任务类必须实现ScheduledOfTask接口");
//            // 可以通过改变数据库数据进而实现动态改变执行周期
//            taskRegistrar.addTriggerTask(((Runnable) task),
//                    triggerContext -> {
//                        String cronExpression = cronRepository.findByCronKey(springScheduledCron.getCronKey()).getCronExpression();
//                        return new CronTrigger(cronExpression).nextExecutionTime(triggerContext);
//                    }
//            );
//        }
    }
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }
}