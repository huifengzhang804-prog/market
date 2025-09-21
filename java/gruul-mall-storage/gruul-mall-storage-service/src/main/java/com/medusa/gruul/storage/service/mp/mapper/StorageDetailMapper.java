package com.medusa.gruul.storage.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * StorageDetailMapper.java
 *
 * @author xiaoq
 * @Description StorageDetailMapper.java
 * @date 2023-07-27 14:45
 */
public interface StorageDetailMapper extends BaseMapper<StorageDetail> {
    void updateStorageDetail(@Param("shopProductKeyList") List<ShopProductKeyDTO> shopProductKeyList, @Param("now") LocalDateTime now);

}
