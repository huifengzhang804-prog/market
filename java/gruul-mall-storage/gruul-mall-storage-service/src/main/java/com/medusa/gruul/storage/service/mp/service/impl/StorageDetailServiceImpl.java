package com.medusa.gruul.storage.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import com.medusa.gruul.storage.service.mp.mapper.StorageDetailMapper;
import com.medusa.gruul.storage.service.mp.service.IStorageDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-07-27 14:44
 */
@Service
@RequiredArgsConstructor
public class StorageDetailServiceImpl extends ServiceImpl<StorageDetailMapper, StorageDetail> implements IStorageDetailService {
    @Override
    public void updateStorageDetail(List<ShopProductKeyDTO> shopProductKeyList) {
        this.baseMapper.updateStorageDetail(shopProductKeyList, LocalDateTime.now());
    }
}
