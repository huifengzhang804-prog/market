package com.medusa.gruul.common.xxl.job;

import cn.hutool.cron.pattern.CronPatternBuilder;
import cn.hutool.cron.pattern.Part;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * cron表达式帮助类
 *
 * @author 张治保
 * date 2023/3/18
 */
public interface CronHelper {

    /**
     * 将LocalDateTime转换为cron表达式
     *
     * @param workTime 任务执行时间
     * @return cron表达式
     */
    static String toCron(LocalDateTime workTime) {
        return CronPatternBuilder.of()
                .set(Part.DAY_OF_MONTH, String.valueOf(workTime.getDayOfMonth()))
                .set(Part.DAY_OF_WEEK, "?")
                .set(Part.MONTH, String.valueOf(workTime.getMonthValue()))
                .set(Part.YEAR, String.valueOf(workTime.getYear()))
                .set(Part.HOUR, String.valueOf(workTime.getHour()))
                .set(Part.MINUTE, String.valueOf(workTime.getMinute()))
                .set(Part.SECOND, String.valueOf(workTime.getSecond()))
                .build();
    }

    /**
     * 将Duration转换为cron表达式
     * 例如：Duration.ofMinutes(30) 返回30分钟后执行一次的cron表达式
     *
     * @param duration 任务执行时间
     *                 例如：Duration.ofMinutes(1) 1分钟后执行
     *                 Duration.ofHours(1) 1小时后执行
     * @return cron表达式
     */
    static String toCron(Duration duration) {
        return toCron(LocalDateTime.now().plus(duration));
    }
}
