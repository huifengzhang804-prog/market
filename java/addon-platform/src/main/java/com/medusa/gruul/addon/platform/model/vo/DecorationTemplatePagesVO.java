package com.medusa.gruul.addon.platform.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>装修模板页面集合VO</p>
 *
 * @author Andy.Yan
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DecorationTemplatePagesVO {

    /**
     * 商城首页(推荐)
     */
    private List<PageVO> recommendedMallHomePages = List.of();

    /**
     * 商城首页(同城)
     */
    private List<PageVO> sameCityMallHomePages = List.of();

    /**
     * 商品分类
     */
    private List<PageVO> productCategoryPages = List.of();

    /**
     * 底部导航
     */
    private List<PageVO> bottomNavigationPages = List.of();

    /**
     * 个人中心
     */
    private List<PageVO> personalCenterPages = List.of();

    /**
     * 自定义页面
     */
    private List<PageVO> customizedPages = List.of();

    /**
     * 店铺首页
     */
    private List<PageVO> shopHomePages = List.of();

    /**
     * 店铺底部导航页面
     */
    private List<PageVO> shopBottomNavigationPages = List.of();

    /**
     * 店铺分类页面
     */
    private List<PageVO> shopCategoryPages = List.of();

    /**
     * 店铺自定义页面
     */
    private List<PageVO> shopCustomizedPages = List.of();

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class PageVO {
        private Long id;
        private String name;

        public static PageVO of(Long id, String name) {
            return new PageVO().setId(id).setName(name);
        }
    }

}
