package com.medusa.gruul.addon.live.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.live.mp.entity.Reservation;
import com.medusa.gruul.addon.live.mp.mapper.ReservationMapper;
import com.medusa.gruul.addon.live.mp.service.ReservationService;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService  {
}
