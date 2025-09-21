package com.medusa.gruul.shop.service.model.vo;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 装修模板页面集合VO
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class DecorationTemplatePagesVO {

    /**
     * 店铺首页
     */
    private List<PageVO> shopHomePages = List.of();

    /**
     * 店铺分类
     */
    private List<PageVO> shopCategoryPages = List.of();

    /**
     * 底部导航
     */
    private List<PageVO> shopBottomNavigationPages = List.of();

    /**
     * 自定义页面
     */
    private List<PageVO> shopCustomPages = List.of();

    @Data
    @Accessors(chain = true)
    public static class PageVO {

        private Long id;
        private String name;

        public static PageVO of(Long id, String name) {
            return new PageVO().setId(id).setName(name);
        }
    }

}