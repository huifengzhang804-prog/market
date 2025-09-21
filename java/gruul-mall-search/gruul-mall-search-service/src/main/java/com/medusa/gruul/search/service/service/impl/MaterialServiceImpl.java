package com.medusa.gruul.search.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.search.service.es.entity.EsMaterialCategoryEntity;
import com.medusa.gruul.search.service.es.entity.EsMaterialEntity;
import com.medusa.gruul.search.service.es.entity.EsPath;
import com.medusa.gruul.search.service.es.mapper.EsMaterialCategoryMapper;
import com.medusa.gruul.search.service.es.mapper.EsMaterialMapper;
import com.medusa.gruul.search.service.model.SearchConst;
import com.medusa.gruul.search.service.model.bo.SourceInfo;
import com.medusa.gruul.search.service.model.dto.*;
import com.medusa.gruul.search.service.model.vo.MaterialSearchSuggestVO;
import com.medusa.gruul.search.service.service.MaterialService;
import com.medusa.gruul.search.service.service.SearchService;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.tika.Tika;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.biz.OrderByParam;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.dromara.easyes.core.toolkit.FieldUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * t_material 素材表服务实现类:
 * todo 不是最优方案  后续可考虑其他方式，比如批量上传 或者直接携带token前端上传，不经过后端
 *
 * @author xiaoq
 * @since : 2022-03-05 10:26
 */
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    /**
     * 文件类型检测工具
     */
    private static final Tika TIKA = new Tika();
    /**
     * 允许的图片格式 前缀
     */
    private static final String ALLOW_IMAGE_PREFIX = "image/";
    /**
     * 允许的视频格式
     */
    private static final String ALLOW_VIDEO = "video/mp4";
    /**
     * 允许的视频格式 quicktime
     */
    private static final String ALLOW_VIDEO_QUICKTIME = "video/quicktime";
    /**
     * 视频格式
     */
    private static final String VIDEO_FORMAT = "mp4";

    /**
     * webp格式
     */
    private static final String IMAGE_WEBP = "image/webp";

    /**
     * 素材名称最大长度
     */
    private static final int MATERIAL_NAME_MAX_LENGTH = 20;
    /**
     * 分类下最大子分类数量
     */
    private static final int MAX_CATEGORY_CHILDREN_NUM = 20;

    /**
     * name字段作为keyword类型查询
     */
    private static final String NAME_KEYWORD = "name.keyword";

    /**
     * 嵌套查询 paths
     */
    private static final String PATHS = "paths";

    /**
     * 嵌套查询 paths.id
     */
    private static final String PATHS_ID = "paths.id";

    /**
     * 分类最大层级
     */
    private static final int MAX_CATEGORY_LEVEL = 3;

    private final SearchService searchService;
    private final EsMaterialMapper esMaterialMapper;
    private final EsMaterialCategoryMapper esMaterialCategoryMapper;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final RestHighLevelClient restHighLevelClient;

    /**
     * 创建分类
     *
     * @param shopId   店铺id
     * @param category 分类信息
     */
    @Redisson(
            name = SearchConst.MATERIAL_CATEGORY_ADD_LOCK,
            key = "#shopId + ':' + (#category.parentId==null?'0':#category.parentId)"
    )
    @Override
    public void category(Long shopId, MaterialCategoryDTO category) {
        String parentId = StrUtil.isNotBlank(category.getParentId()) ? category.getParentId() : null;
        boolean parentNotBlank = StrUtil.isNotBlank(parentId);
        //获取父级分类信息
        EsMaterialCategoryEntity parentCategory =
                StrUtil.isBlank(parentId) ? null :
                        esMaterialCategoryMapper.selectOne(
                                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                                        .select(EsMaterialCategoryEntity::getId, EsMaterialCategoryEntity::getPaths)
                                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                                        .eq(EsMaterialCategoryEntity::getId, parentId)
                        );
        //检查父级分类是否存在
        if (parentNotBlank && parentCategory == null) {
            throw new GlobalException("父级分类不存在");
        }

        //检查分类层级，最高3级
        if (parentNotBlank) {
            EsMaterialCategoryEntity requestEntity = new EsMaterialCategoryEntity();
            requestEntity.setParentId(parentId);
            requestEntity.setShopId(shopId);
            AtomicBoolean levelList = isCategoryLeveMax(requestEntity);
            if (levelList.get()) {
                throw new GlobalException("分类层级已达到最大：" + MAX_CATEGORY_LEVEL);
            }
        }
        //校验父分类下子分类数量是否超过上限
        Long count = esMaterialCategoryMapper.selectCount(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .eq(parentNotBlank, EsMaterialCategoryEntity::getParentId, parentId)
                        .and(!parentNotBlank, qw -> qw.not().exists(EsMaterialCategoryEntity::getParentId))
        );
        if (count >= MAX_CATEGORY_CHILDREN_NUM) {
            throw new GlobalException("当前分类数量超过上限：" + MAX_CATEGORY_CHILDREN_NUM);
        }
        //检查分类名称是否重复
        if (esMaterialCategoryMapper.selectCount(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .select(EsMaterialCategoryEntity::getId)
                        .eq(NAME_KEYWORD, category.getName())
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .eq(parentNotBlank, EsMaterialCategoryEntity::getParentId, parentId)

        ) > 0) {
            throw new GlobalException("分类名称重复");
        }
        //生成当前节点 id 与 path 信息
        String id = IdUtil.getSnowflakeNextIdStr();
        List<EsPath> paths = new ArrayList<>(parentCategory == null ? List.of() : parentCategory.getPaths());
        paths.add(new EsPath().setLevel(paths.size() + 1).setId(id));
        //保存分类信息
        esMaterialCategoryMapper.insert(
                new EsMaterialCategoryEntity()
                        .setId(id)
                        .setParentId(parentId)
                        .setShopId(shopId)
                        .setName(category.getName())
                        .setPaths(paths)
        );
        searchService.refreshIndex(EsMaterialCategoryEntity.class);
    }

    /**
     * 判断分类是否达到最大层级
     */
    private AtomicBoolean isCategoryLeveMax(EsMaterialCategoryEntity entity) {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //嵌套查询
        NestedQueryBuilder nested = QueryBuilders.nestedQuery(
                PATHS
                , QueryBuilders.matchQuery(PATHS_ID, entity.getParentId())
                , ScoreMode.None);
        searchSourceBuilder.query(nested);
        request.source(searchSourceBuilder);
        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new GlobalException("判断分类最大层级异常");
        }
        AtomicBoolean isLevelMax = new AtomicBoolean(false);
        searchResponse.getHits().forEach(hi -> {
            String result = hi.getSourceAsString();
            EsMaterialCategoryEntity esMaterialCategory = FastJson2.convert(result, EsMaterialCategoryEntity.class);
            esMaterialCategory.getPaths().forEach(esPath -> {
                Integer level = esPath.getLevel();
                if (level >= MAX_CATEGORY_LEVEL && esPath.getId().equals(entity.getParentId())) {
                    isLevelMax.set(true);
                }
            });
        });
        return isLevelMax;
    }

    /**
     * 删除分类
     *
     * @param shopId     店铺id
     * @param categoryId 分类id
     */
    @Override
    public void deleteCategory(Long shopId, String categoryId) {
        //检查分类是否存在
        EsMaterialCategoryEntity deleteCategory = esMaterialCategoryMapper.selectOne(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .select(EsMaterialCategoryEntity::getId)
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .eq(EsMaterialCategoryEntity::getId, categoryId)
        );
        if (deleteCategory == null) {
            return;
        }
        //获取分类下所有子分类 id
        List<EsMaterialCategoryEntity> children = esMaterialCategoryMapper.selectList(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .select(EsMaterialCategoryEntity::getId)
                        .nested(
                                FieldUtils.val(EsMaterialCategoryEntity::getPaths),
                                wrapper -> wrapper.eq(FieldUtils.val(EsPath::getId), categoryId)
                        )
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
        );
        Set<String> deleteCategoryIds = CollUtil.emptyIfNull(children).stream().map(
                EsMaterialCategoryEntity::getId).collect(Collectors.toSet());
        deleteCategoryIds.add(categoryId);
        //是否有素材关联到该分类
        if (esMaterialMapper.selectCount(
                EsWrappers.lambdaQuery(EsMaterialEntity.class)
                        .eq(EsMaterialEntity::getShopId, shopId)
                        .in(EsMaterialEntity::getCategoryId, deleteCategoryIds)
        ) > 0) {
            throw new GlobalException("该分类下有素材，无法删除");
        }
        //删除分类
        esMaterialCategoryMapper.delete(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .in(EsMaterialCategoryEntity::getId, deleteCategoryIds)
        );
        searchService.refreshIndex(EsMaterialCategoryEntity.class);
    }

    /**
     * 修改分类名称
     *
     * @param shopId 店铺id
     * @param name   分类名称参数
     */
    @Override
    public void updateCategoryName(Long shopId, MaterialCategoryNameDTO name) {
        //检查分类是否存在
        EsMaterialCategoryEntity categoryEntity = esMaterialCategoryMapper.selectOne(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .eq(EsMaterialCategoryEntity::getId, name.getId())
        );
        if (Objects.isNull(categoryEntity)) {
            throw new GlobalException("分类不存在");
        }
        //校验同级分类下排除自己，是否有同名的分类名称
        String parentId = categoryEntity.getParentId();
        boolean parentNotBlank = StrUtil.isNotBlank(parentId);
        Long sameNameCount = esMaterialCategoryMapper.selectCount(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .and(wrapper -> wrapper.not().eq(EsMaterialCategoryEntity::getId, name.getId()))
                        .eq(parentNotBlank, EsMaterialCategoryEntity::getParentId, parentId)
                        .and(!parentNotBlank, wrapper -> wrapper.not().exists(EsMaterialCategoryEntity::getParentId))
                        .eq(NAME_KEYWORD, name.getName())
        );
        if (sameNameCount > 0) {
            throw new GlobalException("同级分类名称重复");
        }
        //更新分类名称
        Integer update = esMaterialCategoryMapper.update(
                null,
                EsWrappers.lambdaUpdate(EsMaterialCategoryEntity.class)
                        .set(EsMaterialCategoryEntity::getName, name.getName())
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .eq(EsMaterialCategoryEntity::getId, name.getId())
        );
        if (update == null || update == 0) {
            throw new GlobalException("更新失败");
        }
        searchService.refreshIndex(EsMaterialCategoryEntity.class);
    }

    /**
     * 获取子分类列表
     *
     * @param shopId   店铺 id
     * @param parentId 父级分类 id
     * @return 子分类列表
     */
    @Override
    public List<EsMaterialCategoryEntity> getChildrenCategory(Long shopId, String parentId) {
        boolean notBlankParentId = StrUtil.isNotBlank(parentId);
        //获取搜索分类id记录
        String historyId = RedisUtil.getCacheMapValue(SearchConst.CATEGORYID_SELECT_HISTORY, String.valueOf(shopId));
        if (!notBlankParentId) {
            //全部分类包括所有子集
            return getAllCategory(shopId, historyId);
        }
        //查询所有子分类
        List<EsMaterialCategoryEntity> children = esMaterialCategoryMapper.selectList(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .eq(EsMaterialCategoryEntity::getParentId, parentId)
                        .orderByAsc("name.keyword")
        );
        //判断是否有子分类
        if (CollUtil.isNotEmpty(children)) {
            Set<String> parentIds = children.stream().map(EsMaterialCategoryEntity::getId).collect(Collectors.toSet());
            List<EsMaterialCategoryEntity> childrenChildren = esMaterialCategoryMapper.selectList(
                    EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                            .select(EsMaterialCategoryEntity::getParentId)
                            .eq(EsMaterialCategoryEntity::getShopId, shopId)
                            .in(EsMaterialCategoryEntity::getParentId, parentIds)
            );
            Map<String, List<EsMaterialCategoryEntity>> childrenMap = CollUtil.emptyIfNull(childrenChildren).stream()
                    .collect(Collectors.groupingBy(EsMaterialCategoryEntity::getParentId));
            children.forEach(category -> category.setHasChildren(childrenMap.containsKey(category.getId()))
                    .setSelectHistory(category.getId().equals(historyId))
            );
        }
        return children;
    }

    /**
     * 获取所有分类
     *
     * @param shopId    店铺id
     * @param historyId 搜索历史id
     * @return 分类list
     */
    private List<EsMaterialCategoryEntity> getAllCategory(Long shopId, String historyId) {
        List<EsMaterialCategoryEntity> allCategories = esMaterialCategoryMapper.selectList(
                EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                        .eq(EsMaterialCategoryEntity::getShopId, shopId)
                        .orderByAsc("name.keyword")
        );

        if (CollUtil.isEmpty(allCategories)) {
            return Collections.emptyList();
        }

        // 设置历史选择状态
        allCategories.forEach(category -> category.setSelectHistory(category.getId().equals(historyId)));

        Map<Integer, List<EsMaterialCategoryEntity>> categoriesByLevel = allCategories.stream()
                .collect(Collectors.groupingBy(category -> category.getPaths().size()));

        // 一级分类
        List<EsMaterialCategoryEntity> firstCategory = categoriesByLevel.getOrDefault(CommonPool.NUMBER_ONE, Collections.emptyList());
        // 二级分类
        List<EsMaterialCategoryEntity> secondCategory = categoriesByLevel.getOrDefault(CommonPool.NUMBER_TWO, Collections.emptyList());

        // 构建三级分类Map
        Map<String, List<EsMaterialCategoryEntity>> thirdChildrenMap = categoriesByLevel.getOrDefault(CommonPool.NUMBER_THREE, Collections.emptyList())
                .stream()
                .collect(Collectors.groupingBy(EsMaterialCategoryEntity::getParentId));

        // 设置子节点关系
        setChildren(secondCategory, thirdChildrenMap);
        setChildren(firstCategory, secondCategory.stream()
                .collect(Collectors.groupingBy(EsMaterialCategoryEntity::getParentId)));

        return firstCategory;
    }

    /**
     * 设置子节点集合并标记父类是否有子节点
     *
     * @param parentCategories 父节点集合
     * @param childrenMap      子节点（group by parentID）集合
     */
    private void setChildren(List<EsMaterialCategoryEntity> parentCategories, Map<String, List<EsMaterialCategoryEntity>> childrenMap) {
        parentCategories.forEach(parent -> {
            List<EsMaterialCategoryEntity> children = childrenMap.get(parent.getId());
            if (!CollUtil.isEmpty(children)) {
                parent.setChildren(children);
                parent.setHasChildren(true);
            }
        });
    }

    /**
     * 上传素材
     *
     * @param shopId   店铺id
     * @param material 素材信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void material(Long shopId, MaterialDTO material) {
        String categoryId = StrUtil.blankToDefault(material.getCategoryId(), "");
        String categoryName = null;
        //分类不为空的时候，检查分类是否仍然存在
        if (StrUtil.isNotEmpty(categoryId)) {
            EsMaterialCategoryEntity entity = esMaterialCategoryMapper.selectOne(
                    EsWrappers.lambdaQuery(EsMaterialCategoryEntity.class)
                            .select(EsMaterialCategoryEntity::getId)
                            .select(EsMaterialCategoryEntity::getName)
                            .eq(EsMaterialCategoryEntity::getId, categoryId));
            if (entity == null) {
                throw new GlobalException("分类不存在");
            }
            categoryName = entity.getName();
        }
        esMaterialMapper.insertBatch(fileToMaterial(shopId, categoryId, categoryName, material.getFiles()));
        searchService.refreshIndex(EsMaterialEntity.class);
    }

    /**
     * 文件转素材
     *
     * @param categoryId   分类id
     * @param categoryName 分类名称
     * @param files        文件列表
     * @return 素材集合
     */
    private Collection<EsMaterialEntity> fileToMaterial(Long shopId
            , String categoryId
            , String categoryName
            , List<MultipartFile> files) {
        Map<Integer, EsMaterialEntity> indexMaterialMap = new HashMap<>(files.size());
        Map<Integer, UploadParamDTO> uploadParamMap = new HashMap<>(files.size());
        for (int index = 0; index < files.size(); index++) {
            MultipartFile file = files.get(index);
            byte[] fileBytes;
            try {
                fileBytes = file.getBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SourceInfo info = this.validAndGetFormat(index, file.getOriginalFilename(), fileBytes);
            indexMaterialMap.put(
                    index,
                    new EsMaterialEntity()
                            .setCategoryId(categoryId)
                            .setCategoryName(categoryName)
                            .setShopId(shopId)
                            .setUrl(null)
                            .setName(info.getName())
                            .setFormat(info.getFormat())
                            .setSize(info.getWidth() == null ? null : EsMaterialEntity.renderSize(
                                    info.getWidth(), info.getHeight()))
            );
            uploadParamMap.put(index, new UploadParamDTO().setFileBytes(fileBytes).setFormat(info.getFormat()));
        }
        Map<Integer, String> indexFileUrlMap = pigeonChatStatisticsRpcService.batchUpload(uploadParamMap);
        //更新素材 url
        indexMaterialMap.forEach(
                (index, materialEntity) -> materialEntity.setUrl(indexFileUrlMap.get(index))
        );
        return indexMaterialMap.values();
    }

    /**
     * 校验文件类型 并获取文件格式
     *
     * @param index            文件序号
     * @param originalFilename 文件原始名
     * @param fileBytes        文件字节
     * @return 素材源信息 com.medusa.gruul.carrier.pigeon.service.modules.oss.model.bo.SourceInfo
     */
    @Override
    public SourceInfo validAndGetFormat(int index, String originalFilename, byte[] fileBytes) {
        String name = FileUtil.mainName(originalFilename);
        if (name.length() > MATERIAL_NAME_MAX_LENGTH) {
            name = name.substring(0, MATERIAL_NAME_MAX_LENGTH);
        }
        SourceInfo material = new SourceInfo()
                .setName(name);
        String detected = TIKA.detect(fileBytes);
        //如果是 mp4或quicktime 格式 直接返回
        if (detected.equalsIgnoreCase(ALLOW_VIDEO) || detected.equalsIgnoreCase(ALLOW_VIDEO_QUICKTIME)) {
            return material.setFormat(VIDEO_FORMAT)
                    .setWidth(0)
                    .setHeight(0);
        }
        //webp格式，没有尺寸直接返回
        if (detected.equalsIgnoreCase(IMAGE_WEBP)) {
            return material.setFormat(detected.substring(ALLOW_IMAGE_PREFIX.length()))
                    .setWidth(0)
                    .setHeight(0);
        }
        //如果是图片格式 则获取图片宽高
        if (detected.toLowerCase().startsWith(ALLOW_IMAGE_PREFIX)) {
            ImageReader read;
            int width;
            int height;
            try {
                ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(fileBytes));
                 read = ImageIO.getImageReaders(input).next();
                read.setInput(input);
                width = read.getWidth(0);
                height = read.getHeight(0);

//                read = ImageIO.read(new ByteArrayInputStream(fileBytes));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (Objects.isNull(read)) {
                throw new GlobalException(StrUtil.format("第{}张图片宽高获取失败，请检查图片格式", index + 1));
            }
            return material.setFormat(detected.substring(ALLOW_IMAGE_PREFIX.length()))
                    .setWidth(width)
                    .setHeight(height);
        }
        //其他格式不支持
        throw new GlobalException(StrUtil.format("不支持的文件格式：{}", detected));
    }

    /**
     * 素材搜索
     *
     * @param shopId 店铺id
     * @param param  搜索参数
     * @return 素材搜索结果
     */
    @Override
    public IPage<EsMaterialEntity> materialSearch(Long shopId, MaterialSearchParamDTO param) {
        String name = param.getName();
        List<OrderByParam> orders = param.getOrders();
        boolean emptyOrders = CollUtil.isEmpty(orders);
        LambdaEsQueryWrapper<EsMaterialEntity> wrapper = EsWrappers.lambdaQuery(EsMaterialEntity.class)
                .eq(EsMaterialEntity::getShopId, shopId)
                .eq(StrUtil.isNotBlank(param.getCategoryId()), EsMaterialEntity::getCategoryId, param.getCategoryId())
                .eq(StrUtil.isNotBlank(param.getFormat()), EsMaterialEntity::getFormat, param.getFormat())
                .eq(StrUtil.isNotBlank(param.getImgSize()), EsMaterialEntity::getSize, param.getImgSize())
                .and(
                        StrUtil.isNotBlank(name),
                        innerWrapper ->
                                innerWrapper.prefixQuery(EsMaterialEntity::getName, name, 1F)
                                        .or().like(EsMaterialEntity::getName, name, 0.9F)
                                        .or().matchPhrase(EsMaterialEntity::getName, name, 0.8F)
                                        .or().match(EsMaterialEntity::getName, name, 0.5F)
                )
                .orderBy(!emptyOrders, orders)
                .sortByScore(emptyOrders);
        EsPageInfo<EsMaterialEntity> esPageInfo = esMaterialMapper.pageQuery(
                wrapper,
                param.getCurrent(),
                param.getSize()
        );
        //设置搜索分类id记录
        RedisUtil.setCacheMapValue(SearchConst.CATEGORYID_SELECT_HISTORY, String.valueOf(shopId), param.getCategoryId());
        return new Page<EsMaterialEntity>(esPageInfo.getPageNum(), esPageInfo.getPageSize(), esPageInfo.getTotal())
                .setRecords(esPageInfo.getList());
    }

    /**
     * 更新素材分类
     *
     * @param shopId                 店铺 id
     * @param materialCategoryChange 素材分类变更参数
     */
    @Override
    public void updateMaterialCategory(Long shopId, MaterialCategoryChangeDTO materialCategoryChange) {
        String categoryId = StrUtil.nullToEmpty(materialCategoryChange.getCategoryId());
        esMaterialMapper.update(
                null,
                EsWrappers.lambdaUpdate(EsMaterialEntity.class)
                        .set(EsMaterialEntity::getCategoryId, categoryId)
                        .in(EsMaterialEntity::getId, materialCategoryChange.getMaterialIds())
                        .eq(EsMaterialEntity::getShopId, shopId)
        );
        searchService.refreshIndex(EsMaterialEntity.class);
    }

    /**
     * 获取素材推荐检索建议
     *
     * @param shopId     店铺id
     * @param categoryId 分类id
     * @return 检索建议
     */
    @Override
    public MaterialSearchSuggestVO materialSuggest(Long shopId, String categoryId) {
        Map<String, Object> aggregations = searchService.aggregations(
                EsMaterialEntity.class,
                EsWrappers.lambdaQuery(EsMaterialEntity.class)
                        .eq(EsMaterialEntity::getShopId, shopId)
                        .eq(StrUtil.isNotBlank(categoryId), EsMaterialEntity::getCategoryId, categoryId),
                List.of(EsMaterialEntity::getFormat
                        , EsMaterialEntity::getSize
                        , EsMaterialEntity::getCategoryId)
        );
        return FastJson2.convert(aggregations, MaterialSearchSuggestVO.class);
    }

    /**
     * 批量删除素材
     *
     * @param shopId      店铺id
     * @param materialIds 素材id集合
     */
    @Override
    public void deleteMaterial(Long shopId, Set<String> materialIds) {
        esMaterialMapper.delete(
                EsWrappers.lambdaQuery(EsMaterialEntity.class)
                        .eq(EsMaterialEntity::getShopId, shopId)
                        .in(EsMaterialEntity::getId, materialIds)
        );
        searchService.refreshIndex(EsMaterialEntity.class);
    }

    /**
     * 更新素材名称
     *
     * @param shopId 店铺id
     * @param name   素材名称参数
     */
    @Override
    public void updateMaterialName(Long shopId, MaterialNameDTO name) {
        Integer update = esMaterialMapper.update(
                null,
                EsWrappers.lambdaUpdate(EsMaterialEntity.class)
                        .set(EsMaterialEntity::getName, name.getName())
                        .eq(EsMaterialEntity::getShopId, shopId)
                        .eq(EsMaterialEntity::getId, name.getId())
        );
        if (update == null || update == 0) {
            throw new GlobalException("素材不存在");
        }
        searchService.refreshIndex(EsMaterialEntity.class);
    }


}
