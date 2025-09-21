package com.medusa.gruul.global.model.o;

import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author 张治保
 * @since 2023/11/10
 */
@ToString
@Accessors(chain = true)
public class RangeDate extends RangeTemporal<RangeDate, LocalDate> {


    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    public LocalDateTime getStartTime() {
        if (startTime == null && start != null) {
            startTime = _startTime(start);
        }
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (endTime == null && end != null) {
            endTime = _endTime(end);
        }
        return endTime;
    }

    private LocalDateTime _startTime(LocalDate start) {
        return start.atStartOfDay();
    }

    private LocalDateTime _endTime(LocalDate end) {
        return end.atTime(LocalTime.MAX);
    }

    /**
     * 开始时间结束时间转换成时间段
     *
     * @return 时间段
     */
    @Override
    public Duration toDuration() {
        LocalDateTime curStartTime;
        LocalDateTime curEndTime;
        if ((curStartTime = getStartTime()) == null || (curEndTime = getEndTime()) == null) {
            return null;
        }
        return Duration.between(curStartTime, curEndTime);
    }

    @Override
    protected boolean isAfter(@NonNull LocalDate start, @NonNull LocalDate end) {
        return Duration.between(_startTime(start), _endTime(end)).isNegative();
    }
}
