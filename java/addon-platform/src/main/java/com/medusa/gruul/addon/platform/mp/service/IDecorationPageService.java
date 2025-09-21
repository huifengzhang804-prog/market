package com.medusa.gruul.addon.platform.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageCreateDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageModifyDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageOfTemplateDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageQueryDTO;
import com.medusa.gruul.addon.platform.model.vo.DecorationPageDetailVO;
import com.medusa.gruul.addon.platform.model.vo.DecorationPageVO;
import com.medusa.gruul.addon.platform.mp.entity.DecorationPage;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import java.util.List;

/**
 * <p>装修页面接口</p>
 */
public interface IDecorationPageService extends IService<DecorationPage> {


    /**
     * 创建装修页面
     *
     * @param dto {@link DecorationPageCreateDTO}
     * @return {@link DecorationPage#getId()}
     */
    Long create(DecorationPageCreateDTO dto);

    /**
     * 修改装修页面
     *
     * @param dto 参数
     */
    void modify(DecorationPageModifyDTO dto);

    /**
     * 分页查询页面信息
     *
     * @param queryDTO 分页参数
     * @return {@link DecorationPageVO}
     */
    IPage<DecorationPageVO> pageQuery(DecorationPageQueryDTO queryDTO);

    /**
     * 删除页面
     *
     * @param id 页面ID
     */
    void delete(Long id);

    /**
     * 复制页面
     *
     * @param id 页面ID
     */
    void clone(Long id);

    /**
     * 按照页面类型查询所有页面
     *
     * @param dto {@link PageTypeEnum} 查询参数
     * @return {@link DecorationPage}
     */
    List<DecorationPage> listPageByPageType(DecorationPageOfTemplateDTO dto);

    /**
     * 根据ID查询页面
     *
     * @param pageIds 页面ID
     * @return {@link }
     */
    List<DecorationPage> listPageByIds(List<Long> pageIds);

    /**
     * 获取页面详情
     *
     * @param id 页面ID
     * @return {@link DecorationPageDetailVO}
     */
    DecorationPageDetailVO getPageById(Long id);


}
