package com.medusa.gruul.global.model.o;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/5/30
 */
@Getter
@Accessors(chain = true)
@ToString
public class RangeDateTime extends RangeTemporal<RangeDateTime, LocalDateTime> {

}
