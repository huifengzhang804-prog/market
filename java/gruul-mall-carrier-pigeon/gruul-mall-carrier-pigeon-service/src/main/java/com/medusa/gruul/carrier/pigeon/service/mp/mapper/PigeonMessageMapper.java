package com.medusa.gruul.carrier.pigeon.service.mp.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticePageDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.PigeonNoticeVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 公告表 Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-04-26
 */
public interface PigeonMessageMapper extends BaseMapper<PigeonMessage> {
    /**
     * 商家端分页查询消息提醒
     * @param page  分页与查询参数
     * @return 查询结果
     */
    IPage<PigeonNoticeVO> pageNotice(@Param("page") NoticePageDTO page);
}
