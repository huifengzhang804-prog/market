package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.user.api.model.UserCollectVO;
import com.medusa.gruul.user.service.model.dto.CollectDTO;
import com.medusa.gruul.user.service.model.dto.UserCollectQueryDTO;
import java.util.List;

/**
 * 用户收藏 服务类
 *
 * @author WuDi
 * @date: 2022/9/20
 */
public interface UserCollectManageService {

    /**
     * 添加用户收藏
     *
     * @param collectDTO 用户收藏
     */
    void userCollect(CollectDTO collectDTO);

    /**
     * 用户取消收藏
     *
     * @param shopId    店铺id
     * @param productId 产品id
     */
    void cancelUserCollect(Long shopId, Long productId);

    /**
     * 获取用户收藏
     *
     * @param userCollectQuery 分页参数
     * @return 查询结果
     */
    IPage<UserCollectVO> getUserCollectPage(UserCollectQueryDTO userCollectQuery);

    /**
     * 查询用户是否对该商品进行收藏
     *
     * @param shopId    店铺id
     * @param productId 产品id
     * @return true or false
     */
    Boolean findUserIsCollect(Long shopId, Long productId, Long userId);


    /**
     * 查询收藏量最多的商品
     *
     * @param shopId 店铺id
     * @return 商品id集合
     */
    List<Long> getShopProductCollection(Long shopId);

    /**
     * 用户收藏数量包含商品与店铺
     *
     * @param userId 用户userId
     * @return 收藏数量
     */
    Long getUserCollectInfoCount(Long userId);
}
