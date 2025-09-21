package com.medusa.gruul.carrier.pigeon.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticePageDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.PigeonNoticeVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 公告表 服务类
 * </p>
 *
 * @author 张治保
 * @since 2022-04-26
 */
public interface IPigeonMessageService extends IService<PigeonMessage> {

    /**
     * 商家端分页查询消息提醒
     * @param noticePage 分页参数
     * @return 查询结果
     */
    IPage<PigeonNoticeVO> pageNotice(NoticePageDTO noticePage);

}
