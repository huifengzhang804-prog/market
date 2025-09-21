package com.medusa.gruul.addon.freight.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsPrint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 物流打印机param
 *
 * @author xiaoq
 * @Description LogisticsPrintParam.java
 * @date 2022-08-11 16:10
 */
@Getter
@Setter
@ToString
public class LogisticsPrintParam extends Page<LogisticsPrint> {
}
