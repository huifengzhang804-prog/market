package com.medusa.gruul.search.service.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.search.service.es.entity.EsMaterialCategoryEntity;
import com.medusa.gruul.search.service.es.entity.EsMaterialEntity;
import com.medusa.gruul.search.service.model.bo.SourceInfo;
import com.medusa.gruul.search.service.model.dto.*;
import com.medusa.gruul.search.service.model.vo.MaterialSearchSuggestVO;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Set;

/**
 * t_material 素材表服务类
 *
 * @author xiaoq
 */
public interface MaterialService {


    /**
     * 创建分类
     *
     * @param shopId   店铺id
     * @param category 分类信息
     */
    void category(Long shopId, MaterialCategoryDTO category);

    /**
     * 删除分类
     *
     * @param shopId     店铺id
     * @param categoryId 分类id
     */
    void deleteCategory(Long shopId, String categoryId);

    /**
     * 修改分类名称
     *
     * @param shopId 店铺id
     * @param name   分类名称参数
     */
    void updateCategoryName(Long shopId, MaterialCategoryNameDTO name);

    /**
     * 获取子分类列表
     *
     * @param shopId   店铺 id
     * @param parentId 父级分类 id
     * @return 子分类列表
     */
    List<EsMaterialCategoryEntity> getChildrenCategory(Long shopId, String parentId);


    /**
     * 上传素材
     *
     * @param shopId   店铺id
     * @param material 素材信息
     */
    void material(Long shopId, MaterialDTO material);


    /**
     * 校验文件类型 并获取文件格式
     *
     * @param index 文件序号
     * @param originalFilename 文件原始名
     * @param fileBytes        文件字节
     * @return 素材源信息 com.medusa.gruul.carrier.pigeon.service.modules.oss.model.bo.SourceInfo
     */
    SourceInfo validAndGetFormat(int index, String originalFilename, byte[] fileBytes);

    /**
     * 素材搜索
     *
     * @param shopId 店铺id
     * @param param  搜索参数
     * @return 素材搜索结果
     */
    IPage<EsMaterialEntity> materialSearch(Long shopId, MaterialSearchParamDTO param);

    /**
     * 更新素材分类
     *
     * @param shopId                 店铺 id
     * @param materialCategoryChange 素材分类变更参数
     */
    void updateMaterialCategory(Long shopId, MaterialCategoryChangeDTO materialCategoryChange);

    /**
     * 获取素材推荐检索建议
     *
     * @param shopId     店铺id
     * @param categoryId 分类id
     * @return 检索建议
     */
    MaterialSearchSuggestVO materialSuggest(Long shopId, @Nullable String categoryId);

    /**
     * 批量删除素材
     *
     * @param shopId      店铺id
     * @param materialIds 素材id集合
     */
    void deleteMaterial(Long shopId, Set<String> materialIds);

    /**
     * 更新素材名称
     *
     * @param shopId 店铺id
     * @param name   素材名称参数
     */
    void updateMaterialName(Long shopId, MaterialNameDTO name);
}
