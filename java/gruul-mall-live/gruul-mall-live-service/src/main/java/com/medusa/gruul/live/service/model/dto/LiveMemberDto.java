package com.medusa.gruul.live.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/11
 * @Describe 获取直播成员列表接收参数
 */
@Data
public class LiveMemberDto extends Page<Object> {

    /**
     * 搜索条件 用户昵称 / 微信号
     */
    private   String keywords;



}
