package com.medusa.gruul.carrier.pigeon.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageUserVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageUser;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author 张治保
 * @since 2022-10-10
 */
public interface MessageUserMapper extends BaseMapper<MessageUser> {


    /**
     * 分页查询消息用户列表
     * @param adminId 查询的管理员id
     * @param query 分页查询条件
     * @return 分页查询结果
     */
    IPage<MessageUserVO> messageUserPage(@Param("adminId")Long adminId,@Param("query") MessageUserPageQueryDTO query);
}
