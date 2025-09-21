package com.medusa.gruul.addon.freight.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.freight.mp.entity.FreightConf;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 物流快递分页数据
 *
 * @author xiaoq
 * @Description FrightConfParam.java
 * @date 2022-06-07 13:54
 */

@Getter
@Setter
@ToString
public class FrightConfParam extends Page<FreightConf> {
}
