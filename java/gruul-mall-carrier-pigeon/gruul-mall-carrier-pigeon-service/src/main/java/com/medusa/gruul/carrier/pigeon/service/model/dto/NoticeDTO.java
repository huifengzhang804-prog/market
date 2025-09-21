package com.medusa.gruul.carrier.pigeon.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.carrier.pigeon.api.enums.Channel;
import com.medusa.gruul.carrier.pigeon.api.enums.MsgType;
import com.medusa.gruul.carrier.pigeon.api.enums.NoticeType;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IPigeonMessageService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2022/4/27
 */

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class NoticeDTO {


    /**
     * 公告标题
     */
    @NotBlank(message = "标题必须在2和64之间")
    @Size(min = 2, max = 64)
    private String title;

    /**
     * 摘要
     */
    @Size(max = 150)
    private String summary;
    /**
     * 公告内容
     */
    @NotBlank
    private String content;


    /**
     * 更新数据
     */
    public void update(Long id, IPigeonMessageService pigeonMessageService) {
        PigeonMessage notice = pigeonMessageService.getById(id);
        if (notice == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST);
        }
        notice.setTitle(getTitle())
                .setContent(getContent())
                .setUpdateBy(ISecurity.userOpt().get().getId());
        pigeonMessageService.updateById(notice);
    }


    /**
     * 新增消息提醒
     */
    public void save(IPigeonMessageService pigeonMessageService) {
        Long userId = ISecurity.userOpt().get().getId();
        PigeonMessage pigeonNotice = toPigeonNotice()
                .setCreateBy(userId)
                .setUpdateBy(userId);
        boolean success = pigeonMessageService.save(pigeonNotice);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED);
        }
    }


    private PigeonMessage toPigeonNotice() {
        PigeonMessage notice = new PigeonMessage()
                .setPushed(Boolean.FALSE)
                .setType(NoticeType.ANNOUNCEMENT)
                .setSendType(SendType.BROADCAST_SHOP)
                .setChannel(Channel.NOTICE)
                .setMsgType(MsgType.PAGE)
                .setTitle(getTitle())
                .setSummary(getSummary())
                .setContent(getContent());
        Number number = MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(notice);
        long id = number.longValue();
        notice.setUrl(StrUtil.format(notice.getType().getUrlFormat(), id))
                .setId(id);
        return notice;
    }
}
