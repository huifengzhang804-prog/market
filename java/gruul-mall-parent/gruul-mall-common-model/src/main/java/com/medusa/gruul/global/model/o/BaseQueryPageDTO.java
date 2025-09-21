package com.medusa.gruul.global.model.o;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/11/3
 */
@Getter
@Setter
@ToString
public class BaseQueryPageDTO<T> extends Page<T> {

    /**
     * 最后创建时间
     */
    private LocalDateTime lastCreateTime;

}
