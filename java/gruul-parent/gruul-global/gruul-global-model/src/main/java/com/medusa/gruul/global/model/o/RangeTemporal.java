package com.medusa.gruul.global.model.o;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.Temporal;

/**
 * 时间范围对象顶级抽象类
 *
 * @author 张治保
 * @since 2024/5/30
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class RangeTemporal<S extends RangeTemporal<S, T>, T extends Temporal> implements Serializable {

    /**
     * 开始时间
     */
    @NotNull
    protected T start;

    /**
     * 结束时间
     */
    @NotNull
    protected T end;


    @SuppressWarnings("unchecked")
    public final S setStart(T start) {
        validateIt(start, end);
        this.start = start;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public final S setEnd(T end) {
        validateIt(start, end);
        this.end = end;
        return (S) this;
    }

    /**
     * 开始时间结束时间转换成时间段
     *
     * @return 时间段
     */
    public Duration toDuration() {
        if (start == null || end == null) {
            return null;
        }
        return Duration.between(start, end);

    }

    /**
     * 验证时间范围 设置是否正确
     *
     * @param start 开始时间
     * @param end   结束时间
     */
    private void validateIt(T start, T end) {
        if (start == null || end == null) {
            return;
        }
        if (isAfter(start, end)) {
            throw new TimeRangeException();
        }
    }

    /**
     * 判断开始时间是否在结束时间之后
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 是否在结束时间之后
     */
    protected boolean isAfter(@NonNull T start, @NonNull T end) {
        return Duration.between(start, end).isNegative();
    }

    /**
     * 时间范围异常
     */
    public static final class TimeRangeException extends RuntimeException {
        public TimeRangeException() {
            super("start time cannot be greater than end time");
        }
    }
}
