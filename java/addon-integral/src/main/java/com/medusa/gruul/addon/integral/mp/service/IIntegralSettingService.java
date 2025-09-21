package com.medusa.gruul.addon.integral.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.integral.model.dto.IntegralSettingDTO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralSetting;

/**
 * @author shishuqian
 * date 2023/2/9
 * time 11:12
 **/
public interface IIntegralSettingService extends IService<IntegralSetting> {

    /**
     * 添加积分设置
     * 如果数据库存在一条积分设置，则修改
     * 如果数据库没有积分设置，则新增
     * 数据库里只能有一条积分设置数据
     *
     * @param integralSettingDTO 积分规则dto
     * @return 是否成功
     */
    boolean add(IntegralSettingDTO integralSettingDTO);

    /**
     * 获取积分设置
     *
     * @return 积分设置信息
     * @author shishuqian
     */
    IntegralSetting getSetting();

}
