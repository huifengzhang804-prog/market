package com.medusa.gruul.carrier.pigeon.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticeDTO;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticePageDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.PigeonNoticeVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;

/**
 * 公告服务
 * @author 张治保
 * date 2022/4/26
 */
public interface NoticeService {

    /**
     * 分页查询消息提醒
     *
     * @param noticePage 分页参数
     * @return 查询结果
     */
    IPage<PigeonMessage> pageNoticePlatform(NoticePageDTO noticePage);

    /**
     * 商家端分页查询消息提醒
     *
     * @param noticePage 分页参数
     * @return 查询结果
     */
    IPage<PigeonNoticeVO> pageNotice(NoticePageDTO noticePage);

    /**
     * 新增消息通知
     *
     * @param notice 消息数据
     */
    void newNotice(NoticeDTO notice);

    /**
     * 编辑消息通知
     * @param noticeId 消息id
     * @param notice 消息数据
     */
    void editNotice(Long noticeId, NoticeDTO notice);

    /**
     * 推送消息
     * @param noticeId 消息id
     */
    void pushNotice(Long noticeId);

    /**
     * 删除通知
     * @param noticeId 通知id
     */
    void deleteNotice(Long noticeId);

    /**
     * 查询消息详情
     * @param messageId 消息id
     * @return 消息详情
     */
    PigeonNoticeVO noticeById(Long messageId);
}
