package com.medusa.gruul.carrier.pigeon.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.enums.NoticeType;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/4/26
 */
@Getter
@Setter
@ToString
public class NoticePageDTO extends Page<PigeonMessage> {

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 查询类型
     */
    private NoticeType type;
    /**
     * 是否已读
     */
    private Boolean read;

}
