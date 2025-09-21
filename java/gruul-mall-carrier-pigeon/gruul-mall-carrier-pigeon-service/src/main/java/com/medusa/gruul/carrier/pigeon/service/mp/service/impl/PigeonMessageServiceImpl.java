package com.medusa.gruul.carrier.pigeon.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticePageDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.PigeonNoticeVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;
import com.medusa.gruul.carrier.pigeon.service.mp.mapper.PigeonMessageMapper;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IPigeonMessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公告表 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-04-26
 */
@Service
public class PigeonMessageServiceImpl extends ServiceImpl<PigeonMessageMapper, PigeonMessage> implements IPigeonMessageService {

    @Override
    public IPage<PigeonNoticeVO> pageNotice(NoticePageDTO noticePage) {
        return baseMapper.pageNotice(noticePage);
    }
}
