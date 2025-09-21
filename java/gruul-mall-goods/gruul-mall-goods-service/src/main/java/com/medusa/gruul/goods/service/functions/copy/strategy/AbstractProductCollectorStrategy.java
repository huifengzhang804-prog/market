package com.medusa.gruul.goods.service.functions.copy.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.entity.CollectProductData;
import com.medusa.gruul.goods.api.enums.CopyGoodsType;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.service.functions.copy.ProductCollectorFactory;
import com.medusa.gruul.goods.service.model.param.CopyGoodsParam;
import com.medusa.gruul.goods.service.model.vo.CopyProductVO;
import com.medusa.gruul.goods.service.model.vo.CopySkuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
abstract class AbstractProductCollectorStrategy implements IStrategy<CopyGoodsType, CopyGoodsParam, CopyProductVO> {

    /**
     * 正则表达式匹配所有 video 和 img 标签的 src 属性值
     */
    protected static final Pattern ASSERTS_SRC_PATTERN = Pattern.compile("<(?:img|video)[^>]*?src\\s*=\\s*\"([^\"]*)\"",
            Pattern.CASE_INSENSITIVE);

    /**
     * 规格最大长度
     */
    private static final int SPEC_MAX_LENGTH = 64;

    /**
     * 商品详情描述中的资源替换
     *
     * @param desc        商品详情描述
     * @param urlsMapping 资源映射
     * @return 替换后的商品详情描述
     */
    protected String detailAssetsReplace(String desc, Map<String, String> urlsMapping) {
        if (StrUtil.isEmpty(desc)) {
            return desc;
        }
        Matcher matcher = ASSERTS_SRC_PATTERN.matcher(desc);
        if (!matcher.find()) {
            return desc;
        }
        if (CollUtil.isEmpty(urlsMapping)) {
            return desc;
        }
        return matcher.replaceAll(
                matchResult -> {
                    String originalUrl = matchResult.group(CommonPool.NUMBER_ONE);
                    String formatUrl = formatUrl(originalUrl);
                    return matchResult.group()
                            .replace(
                                    originalUrl,
                                    urlsMapping.getOrDefault(formatUrl, formatUrl)
                            );
                }
        );
    }

    @Override
    public CopyProductVO execute(CopyGoodsType type, CopyGoodsParam param) {
        // 请求获取数据
        JSONObject data = getDataJson(type, param);
        //数据结果检查
        if (!GoodsConstant.SUCCESS_CODE.equals(data.getString(GoodsConstant.RET_CODE))) {
            log.error("商品复制失败：{}，{}", type, data);
            throw GoodsError.API_EXCEPTION_99API.exception("99API ", data.getString(GoodsConstant.DATA));
        }
        // 处理数据
        CopyProductVO product = this.getProduct(data.getJSONObject(GoodsConstant.DATA));
        // 后置处理
        this.postUpdate(product);
        return product;
    }

    /**
     * 后置处理 处理商品名与主图
     *
     * @param product 商品拷贝信息
     */
    private void postUpdate(CopyProductVO product) {
        String productName = product.getName();
        // 截取商品名称
        if (StrUtil.isNotEmpty(productName)) {
            product.setName(cutString(productName, 64));
        }
        // 截取主图
        List<String> images = product.getOriginalImgUrls();
        if (CollUtil.isNotEmpty(images)) {
            product.setOriginalImgUrls(images.stream().limit(CommonPool.NUMBER_SIX).toList());
        }
        // 主图上传到oss 并重新设置商品主图
        this.assetsHandle(product);
    }

    /**
     * 获取商品信息 抽象方法
     *
     * @param data 原始 json 数据
     * @return 商品信息
     */
    protected abstract CopyProductVO getProduct(JSONObject data);

    /**
     * 商品链接地址处理
     *
     * @param goodsUrl 商品链接地址
     * @return 新 url
     */
    protected abstract String url(String goodsUrl);


    /**
     * 获取数据
     *
     * @param type  类型
     * @param param 参数
     * @return 数据
     */
    protected JSONObject getDataJson(CopyGoodsType type, CopyGoodsParam param) {
        String goodsUrl = param.getGoodsUrl();
        String itemId = url(goodsUrl);
        if (StrUtil.isEmpty(itemId)) {
            log.error("不合法的商品链接地址:{}", goodsUrl);
            throw GoodsError.LINK_ERROR.exception();
        }
        JSONObject result;
        if (ProductCollectorFactory.useCache) {
            result = queryCacheData(type.getType(), itemId);
            if (Objects.nonNull(result)) {
                return result;
            }
        }
        String url = StrUtil.format(GoodsConstant.COPY_GOODS_URL, type.getType(), ProductCollectorFactory.copyApiKey,
                itemId);
        String body;
        try (HttpResponse response = HttpUtil.createGet(url).timeout(1000 * 60).execute()) {
            body = response.body();
        }
        try {
            result = JSON.parseObject(body);
            if (ProductCollectorFactory.useCache) {
                //判断采集到的数据状态值正确才保存
                if (GoodsConstant.SUCCESS_CODE.equals(result.getString(GoodsConstant.RET_CODE))) {
                    saveCollectData(body, itemId, type.getType());
                }

            }
            return result;
        } catch (Exception ex) {
            log.error("商品复制失败:{}，{}", type, body);
            throw ex;
        }
    }

    /**
     * 保存三方收集到的缓存数据
     *
     * @param body   三方采集到的数据
     * @param itemId 商品ID
     * @param type   商品类型
     */
    private void saveCollectData(String body, String itemId, String type) {
        CollectProductData collectProductData = new CollectProductData();
        collectProductData.setType(type);
        collectProductData.setTermId(itemId);
        collectProductData.setContent(body);
        ProductCollectorFactory.globalExecutor.execute(() -> {
            TenantShop.disable(() -> {
                ProductCollectorFactory.collectProductDataService.save(collectProductData);
            });
        });
    }

    /**
     * 查询缓存中的数据
     *
     * @param type
     * @param itemId
     * @return
     */
    private JSONObject queryCacheData(String type, String itemId) {
        CollectProductData collectProductData = TenantShop.disable(() -> ProductCollectorFactory.collectProductDataService.getBaseMapper()
                .selectOne(new LambdaQueryWrapper<>(CollectProductData.class)
                        .eq(CollectProductData::getType, type)
                        .eq(CollectProductData::getTermId, itemId)));

        if (collectProductData == null) {
            return null;
        }
        return JSONObject.parse(collectProductData.getContent());
    }


    /**
     * 资源上传到oss 并重新设置商品主图
     *
     * @param product 商品拷贝信息
     */
    private void assetsHandle(CopyProductVO product) {
        if (product == null) {
            return;
        }
        // 获取商品详情中所有的 静态资源的（img/video） 标签的 src 属性值
        Set<String> urls = getAssertsUrl(product.getDetail());
        //商品视频
        String videoUrl = product.getVideoUrl();
        boolean notEmptyVideo = StrUtil.isNotEmpty(videoUrl);
        if (notEmptyVideo) {
            urls.add(formatUrl(videoUrl));
        }
        // 获取商品主图url列表
        List<String> originalImgUrls = product.getOriginalImgUrls();
        boolean notEmptyPics = CollUtil.isNotEmpty(originalImgUrls);
        if (notEmptyPics) {
            originalImgUrls.forEach(
                    url -> urls.add(formatUrl(url))
            );
        }
        //主图 url
        String pic = product.getPic();
        boolean notEmptyPic = StrUtil.isNotEmpty(pic);
        if (notEmptyPic) {
            urls.add(formatUrl(pic));
        }
        //sku 图片
        List<CopySkuVO> skus = CollUtil.emptyIfNull(product.getSkus());
        skus.forEach(
                sku -> {
                    String image = sku.getImage();
                    if (StrUtil.isNotEmpty(image)) {
                        urls.add(formatUrl(image));
                    }
                }
        );
        //上传图片到oss 并获取新的Url
//        Map<String, String> ossUrlMap = Maps.newHashMapWithExpectedSize(urls.size());
//
//        try {
//            ossUrlMap = ProductCollectorFactory.pigeonChatStatisticsRpcService.batchUploadUrls(urls);
//        } catch (Exception ex) {
//            log.error("商品资源上传失败", ex);
//            log.error("urls:{}", urls);
//            throw ex;
//        }
        //将urls分成每5个元素的一个小列表
        // 上传图片到oss并获取新的Url
        Map<String, String> ossUrlMap = Maps.newHashMapWithExpectedSize(urls.size());

        try {
            // 将大的URL列表分成每5个一组的小列表
            List<List<String>> batches = CollUtil.split(urls, 5);
            // 创建所有异步任务
            List<CompletableFuture<Map<String, String>>> futures = batches.stream()
                    .map(batch -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return ProductCollectorFactory.pigeonChatStatisticsRpcService.batchUploadUrls(new HashSet<>(batch));
                        } catch (Exception e) {
                            log.error("部分商品资源上传失败, batch: {}", batch, e);
                            return Collections.<String, String>emptyMap(); // 返回空map而不是抛出异常，继续处理其他批次
                        }
                    }))
                    .collect(Collectors.toList());

            // 等待所有异步任务完成并合并结果
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // 合并所有结果到ossUrlMap
            futures.forEach(future -> {
                try {
                    Map<String, String> batchResult = future.get();
                    if (batchResult != null) {
                        ossUrlMap.putAll(batchResult);
                    }
                } catch (Exception e) {
                    log.error("获取异步结果时出错", e);
                }
            });

        } catch (Exception ex) {
            log.error("商品资源上传失败", ex);
            log.error("urls:{}", urls);
            throw ex;
        }
        //商品详情图片替换
        product.setDetail(detailAssetsReplace(product.getDetail(), ossUrlMap));
        //商品视频url替换
        if (notEmptyVideo) {
            String formatUtl = formatUrl(videoUrl);
            product.setVideoUrl(ossUrlMap.getOrDefault(formatUtl, formatUtl));
        }
        //主图url列表替换
        if (notEmptyPics) {
            originalImgUrls = originalImgUrls.stream().map(originalUrl -> {
                String formatUrl = formatUrl(originalUrl);
                return ossUrlMap.getOrDefault(formatUrl, formatUrl);
            }).toList();
            product.setAlbumPics(StrUtil.join(StrPool.COMMA, originalImgUrls));
        }
        //主图替换
        if (notEmptyPic) {
            String formatUrl = formatUrl(pic);
            product.setPic(ossUrlMap.getOrDefault(formatUrl, formatUrl));
        }
        if (StrUtil.isEmpty(product.getPic()) && CollUtil.isNotEmpty(originalImgUrls)) {
            product.setPic(originalImgUrls.get(CommonPool.NUMBER_ZERO));
        }
        //sku 图片替换
        skus.forEach(
                sku -> {
                    String image = sku.getImage();
                    if (StrUtil.isEmpty(image)) {
                        return;
                    }
                    String formatUrl = formatUrl(image);
                    sku.setImage(ossUrlMap.getOrDefault(formatUrl, formatUrl));
                }
        );
    }

    /**
     * 字符串截取
     *
     * @param input     字符串
     * @param maxLength 截取最大长度
     */
    private String cutString(String input, int maxLength) {
        return StrUtil.subPre(input, maxLength);
    }

    /**
     * 截取规格组名
     *
     * @param original 原始名称
     */
    protected String subGroupName(String original) {
        if (StrUtil.isEmpty(original)) {
            return StrUtil.EMPTY;
        }
        return cutString(original, CommonPool.NUMBER_TEN);
    }

    /**
     * 截取规格名
     *
     * @param original 原始名称
     */
    protected String subSpecName(String original) {
        if (StrUtil.isEmpty(original)) {
            return StrUtil.EMPTY;
        }
        return cutString(original, SPEC_MAX_LENGTH);
    }

    /**
     * 获取商品详情中所有的 静态资源的（img/video） 标签的 src 属性值
     *
     * @param desc 商品详情描述
     * @return 静态资源的（img/video） 标签的 src 属性值
     */
    protected Set<String> getAssertsUrl(String desc) {
        Set<String> assertsUrls = CollUtil.newHashSet();
        if (StrUtil.isEmpty(desc)) {
            return assertsUrls;
        }
        Matcher matcher = ASSERTS_SRC_PATTERN.matcher(desc);
        while (matcher.find()) {
            assertsUrls.add(
                    formatUrl(matcher.group(CommonPool.NUMBER_ONE))
            );
        }
        return assertsUrls;
    }

    /**
     * url 格式化 默认判断是否为http开头 如果不是则添加https: 开头 如果不满足需求请重写此方法
     *
     * @param originalUrl 原始url
     * @return 格式化后的url
     */
    protected String formatUrl(String originalUrl) {
        if (originalUrl.startsWith("http")) {
            return originalUrl;
        }
        return "https:" + originalUrl;
    }

    //处理商品详情
    public String dealDescImgs(List<String> imgList) {

        //提取不同详情格式的图片

        StringBuilder sb = new StringBuilder();
        sb.append("<p>");
        imgList.forEach(img -> {
            sb.append("<img src=\"").append(img).append("\">");
        });
        sb.append("</p>");
        return sb.toString();
    }


}