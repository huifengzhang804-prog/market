package com.medusa.gruul.carrier.pigeon.service.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.enums.NoticeType;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.carrier.pigeon.api.model.StompTargetsMessage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticeDTO;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticePageDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.PigeonNoticeVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonShopMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IPigeonMessageService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IPigeonShopMessageService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSendService;
import com.medusa.gruul.carrier.pigeon.service.service.NoticeService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2022/4/26
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final MessageSendService noticeSendService;
    private final IPigeonMessageService pigeonMessageService;
    private final IPigeonShopMessageService pigeonShopMessageService;

    @Override
    public IPage<PigeonMessage> pageNoticePlatform(NoticePageDTO noticePage) {
        String keywords = noticePage.getKeywords();
        return pigeonMessageService.lambdaQuery()
                .eq(PigeonMessage::getType, NoticeType.ANNOUNCEMENT)
                .like(StrUtil.isNotBlank(keywords), PigeonMessage::getTitle, keywords)
                .orderByAsc(PigeonMessage::getPushed)
                .orderByDesc(PigeonMessage::getUpdateTime)
                .page(new Page<>(noticePage.getCurrent(), noticePage.getSize()));
    }

    @Override
    public IPage<PigeonNoticeVO> pageNotice(NoticePageDTO noticePage) {
        return pigeonMessageService.pageNotice(noticePage);
    }


    @Override
    public void newNotice(NoticeDTO notice) {
        notice.save(pigeonMessageService);
    }

    @Override
    public void editNotice(Long noticeId, NoticeDTO notice) {
        notice.update(noticeId, pigeonMessageService);
    }

    @Override
    public void pushNotice(Long noticeId) {
        PigeonMessage notice = pigeonMessageService.lambdaQuery()
                .eq(PigeonMessage::getId, noticeId)
                .eq(PigeonMessage::getType, NoticeType.ANNOUNCEMENT)
                .one();
        if (notice.getPushed()) {
            return;
        }
        notice.setPushed(Boolean.TRUE);
        pigeonMessageService.updateById(notice);

        StompTargetsMessage stompMessage = new StompTargetsMessage();
        stompMessage.setNoticeType(notice.getType())
                .setSendType(notice.getSendType())
                .setChannel(notice.getChannel())
                .setMsgType(notice.getMsgType())
                .setTitle(notice.getTitle())
                .setSummary(notice.getSummary())
                .setUrl(notice.getUrl())
                .setContent(notice.getContent())
                .setCreateBy(notice.getCreateBy());
        noticeSendService.pushMessage(noticeId, stompMessage);
    }

    @Override
    public void deleteNotice(Long noticeId) {
        boolean success = pigeonMessageService.removeById(noticeId);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED);
        }
        pigeonShopMessageService.lambdaUpdate()
                .eq(PigeonShopMessage::getMessageId, noticeId)
                .remove();
    }

    @Override
    public PigeonNoticeVO noticeById(Long messageId) {
        PigeonMessage message = pigeonMessageService.getById(messageId);
        if (message == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST);
        }
        SendType sendType = message.getSendType();

        /*
         * 查询是否已有对应已读关系
         */
        Long shopId = ISecurity.userOpt().get().getShopId();
        PigeonShopMessage shopNotice = pigeonShopMessageService.lambdaQuery()
                .eq(PigeonShopMessage::getMessageId, messageId)
                .eq(PigeonShopMessage::getShopId, shopId)
                .one();
        if (sendType.getMarked() && shopNotice == null) {
            throw new GlobalException(SecureCodes.ACCESS_DENIED.code(), SecureCodes.ACCESS_DENIED.msg());
        }
        /*
         * 不为空 且未读 则更新为已读
         */
        if (shopNotice != null && BooleanUtil.isFalse(shopNotice.getRead())) {
            boolean success = pigeonShopMessageService.updateById(shopNotice.setRead(Boolean.TRUE));
            if (!success) {
                throw new GlobalException(SystemCode.DATA_UPDATE_FAILED);
            }
        }
        /*
         * 如果没有对应关系则 新增 并设置为已读
         */
        if (shopNotice == null) {
            shopNotice = new PigeonShopMessage()
                    .setMessageId(messageId)
                    .setShopId(shopId)
                    .setRead(Boolean.TRUE);
            boolean success = pigeonShopMessageService.save(shopNotice);
            if (!success) {
                throw new GlobalException(SystemCode.DATA_NOT_EXIST);
            }
        }
        return new PigeonNoticeVO()
                .setId(messageId)
                .setRead(Boolean.TRUE)
                .setType(message.getType())
                .setSendType(message.getSendType())
                .setChannel(message.getChannel())
                .setMsgType(message.getMsgType())
                .setTitle(message.getTitle())
                .setSummary(message.getSummary())
                .setContent(message.getContent())
                .setCreateBy(message.getCreateBy())
                .setUpdateBy(message.getUpdateBy())
                .setCreateTime(message.getCreateTime())
                .setUpdateTime(message.getUpdateTime());
    }
}
