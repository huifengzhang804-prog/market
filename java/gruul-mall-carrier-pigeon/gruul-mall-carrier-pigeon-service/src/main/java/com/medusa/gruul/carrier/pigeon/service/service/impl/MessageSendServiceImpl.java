package com.medusa.gruul.carrier.pigeon.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.carrier.pigeon.api.model.StompTargetsMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonShopMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IPigeonMessageService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IPigeonShopMessageService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageSendService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/5/5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSendServiceImpl implements MessageSendService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final IPigeonMessageService pigeonMessageService;
	private final IPigeonShopMessageService pigeonShopMessageService;

	@Override
	public void saveAndPushMessage(StompTargetsMessage message) {
		Long messageId = this.saveMessage(message);
		this.pushMessage(messageId, message);
	}

	@Override
	public void pushMessage(Long msgId, StompTargetsMessage message) {
		message.checkParam();
		message.setMessageId(msgId);
		SendType sendType = message.getSendType();
		Set<Long> targets = message.getTargets();
		message.hideTargets();
		if (message.getSendType().getMarked()) {
			targets.forEach(
					target -> simpMessagingTemplate.convertAndSend(sendType.getDestination(target), message)
			);
			return;
		}
		simpMessagingTemplate.convertAndSend(sendType.getDestination(), message);

	}

	@Override
	@DSTransactional
	public Long saveMessage(StompTargetsMessage message) {
		message.checkParam();
		SendType sendType = message.getSendType();
		PigeonMessage pigeonMessage = new PigeonMessage()
				.setPushed(Boolean.TRUE)
				.setType(message.getNoticeType())
				.setSendType(sendType)
				.setChannel(message.getChannel())
				.setMsgType(message.getMsgType())
				.setTitle(message.getTitle())
				.setSummary(message.getSummary())
				.setUrl(message.getUrl())
				.setCreateBy(message.getCreateBy())
				.setUpdateBy(message.getCreateBy());
		boolean success = pigeonMessageService.save(pigeonMessage);
		if (!success) {
			throw new GlobalException(SystemCode.DATA_ADD_FAILED);
		}
		Long messageId = pigeonMessage.getId();
		if (!sendType.getMarked()) {
			return messageId;
		}
		/*
		 * 指定店铺发送则保存消息店铺对应关系
		 */
		Set<Long> targets = message.getTargets();
		List<PigeonShopMessage> shopMessages = targets.stream().map(
				target -> new PigeonShopMessage()
						.setShopId(target)
						.setMessageId(messageId)
						.setRead(Boolean.FALSE)
		).collect(Collectors.toList());
		pigeonShopMessageService.saveBatch(shopMessages);
		return messageId;
	}


}
