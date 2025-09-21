package com.medusa.gruul.addon.freight.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.constant.FreightErrorCode;
import com.medusa.gruul.addon.freight.model.dto.LogisticsBaseModelDTO;
import com.medusa.gruul.addon.freight.model.dto.LogisticsIncludePostageDTO;
import com.medusa.gruul.addon.freight.model.dto.LogisticsTemplateDTO;
import com.medusa.gruul.addon.freight.model.enums.ValuationModel;
import com.medusa.gruul.addon.freight.model.param.LogisticsTemplateParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsTemplateVO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsBaseModel;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsIncludePostage;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsTemplate;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsBaseModelService;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsIncludePostageService;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsTemplateService;
import com.medusa.gruul.addon.freight.service.LogisticsTemplateInfoService;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.SkuInfoParam;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaoq
 * description
 * date 2022-05-27 17:21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsTemplateInfoServiceImpl implements LogisticsTemplateInfoService {

    private final ILogisticsTemplateService logisticsTemplateService;

    private final ILogisticsBaseModelService logisticsBaseModelService;

    private final ILogisticsIncludePostageService logisticsIncludePostageService;

    private final GoodsRpcService goodsRpcService;


    /**
     * 物流模板获取
     *
     * @param logisticsTemplateParam 物流模板查询param
     */
    @Override
    public IPage<LogisticsTemplateVO> getTemplateList(LogisticsTemplateParam logisticsTemplateParam) {
        return logisticsTemplateService.getTemplateList(logisticsTemplateParam);
    }


    /**
     * 物流模板获取 by id
     *
     * @param id 物流模板id
     * @return 物流模板信息
     */
    @Override
    public LogisticsTemplateVO getTemplateInfo(Long id) {
        return logisticsTemplateService.getTemplateInfo(id);
    }

    @Override
    public Map<String, BigDecimal> freightCalculation(PlatformFreightParam platformFreight) {
        Map<String, BigDecimal> money = new HashMap<>(CommonPool.NUMBER_EIGHT);
        //省市区
        List<String> area = platformFreight.getArea();
        // key shopId:logisticsId  value 当前logisticsId总运费
        platformFreight.getShopFreights()
                .forEach(shopFreights -> {
                    Long shopId = shopFreights.getShopId();
                    shopFreights.getFreights()
                            .forEach(
                                    logisticsFreights -> freightCalculateByLogisticsId(
                                            shopId,
                                            logisticsFreights.getTemplateId(),
                                            logisticsFreights.getSkuInfos(),
                                            area,
                                            money,
                                            BooleanUtil.isTrue(platformFreight.getFreeRight())
                                    )
                            );
                });
        return money;

    }

    /**
     * 运费计算 by logisticsId
     *
     * @param logisticsId 运费模板id
     * @param skuInfos    商品sku信息
     * @param shopId      商店id
     * @param area        用户所在省市区
     * @param money       运费金额 <key:(shop:物流模板id) : value(运费金额)>
     * @param freeRight   是否拥有会员免运费
     */
    private void freightCalculateByLogisticsId(Long shopId, Long logisticsId, List<SkuInfoParam> skuInfos, List<String> area, Map<String, BigDecimal> money, boolean freeRight) {
        if (logisticsId == CommonPool.NUMBER_ZERO) {
            money.put(shopId + StrPool.COLON + logisticsId, BigDecimal.ZERO);
            return;
        }
        // 基础物流模板获取
        LogisticsBaseModel logisticsBaseModel =
                Option.of(
                        ISystem.shopId(shopId,
                                () -> {
                                    //不包含 市？ 不包含为直辖市
                                    boolean notContainsCity = area.size() <= CommonPool.NUMBER_TWO;
                                    return logisticsBaseModelService.lambdaQuery()
                                            .eq(LogisticsBaseModel::getLogisticsId, logisticsId)
                                            .apply(
                                                    "JSON_CONTAINS(region_json, '{" +
                                                            "\"upperName\":\"" + area.get(CommonPool.NUMBER_ZERO) + "\"" +
                                                            (notContainsCity ? StrUtil.EMPTY : (",\"lowerName\":\"" + area.get(CommonPool.NUMBER_ONE) + "\"")) +
                                                            "}')"
                                            )
                                            .last(SqlHelper.SQL_LIMIT_1)
                                            .one();
                                }
                        )
                ).getOrElseThrow(() -> new GlobalException("所选商品不在邮寄范围内", FreightErrorCode.FREIGHT_DATA_NOT_SUPPORT, new ShopProductSkuKey().setShopId(shopId)));
        //在配送范围，且 有免运费权益
        if (freeRight) {
            money.put(shopId + StrPool.COLON + logisticsId, BigDecimal.ZERO);
            return;
        }
        //运费模板
        LogisticsTemplate logisticsTemplate = Option.of(ISystem.shopId(shopId, () -> logisticsTemplateService.getById(logisticsId)))
                .getOrElseThrow(() -> new GlobalException("当前物流快递模板不存在", FreightErrorCode.FREIGHT_DATA_NOT_EXIST, new ShopProductSkuKey().setShopId(shopId)));

        ValuationModel valuationModel = logisticsTemplate.getValuationModel();
        switch (logisticsTemplate.getChoiceConditionPostage()) {
            //不包邮
            case NO_FREE_SHIPPING ->
                    baseAggregation(shopId, logisticsId, skuInfos, money, logisticsBaseModel, valuationModel);
            //指定条件包邮
            case CONDITION_FREE_SHIPPING ->
                    conditionAggregation(shopId, logisticsId, skuInfos, area, money, logisticsBaseModel, valuationModel);
            default -> {
            }
        }

    }

    /**
     * 根据指定条件包邮模板计算运费
     *
     * @param logisticsId    物流模板id
     * @param skuInfos       商品信息
     * @param code           用户所在省市区
     * @param shopId         商店id
     * @param money          运费金额 <key:(shop:物流模板id) : value(运费金额)>
     * @param valuationModel 计费模式
     */
    private void conditionAggregation(Long shopId, Long logisticsId, List<SkuInfoParam> skuInfos, List<String> code, Map<String, BigDecimal> money, LogisticsBaseModel logisticsBaseModel, ValuationModel valuationModel) {
        LogisticsIncludePostage logisticsIncludePostage = ISystem.shopId(shopId,
                () -> logisticsIncludePostageService.lambdaQuery().eq(LogisticsIncludePostage::getLogisticsId, logisticsId)
                        .like(LogisticsIncludePostage::getRegion, code.get(CommonPool.NUMBER_ZERO))
                        .like(LogisticsIncludePostage::getRegion, code.get(CommonPool.NUMBER_ONE)).last(CommonPool.SQL_LIMIT_1).one()
        );
        //不在指定包邮区域内 使用基础模板
        if (logisticsIncludePostage == null) {
            baseAggregation(shopId, logisticsId, skuInfos, money, logisticsBaseModel, valuationModel);
            return;
        }
        // 获取总件数
        int total = skuInfos.stream().mapToInt(SkuInfoParam::getNum).sum();
        // 获取总金额
        BigDecimal totalPrice = skuInfos.stream().reduce(BigDecimal.ZERO, (list, list1) -> list.add(new BigDecimal(list1.getNum().toString()).multiply(list1.getPrice())), BigDecimal::add);

        //获取总重量
        BigDecimal totalWeight = skuInfos.stream().reduce(BigDecimal.ZERO, (list, list1) -> list.add(new BigDecimal(list1.getNum().toString()).multiply(list1.getWeight())), BigDecimal::add);
        switch (logisticsIncludePostage.getPostType()) {
            case PKGS:
                //件数
                if (total > logisticsIncludePostage.getPieceNum()) {
                    money.put(shopId + StrPool.COLON + logisticsId, BigDecimal.ZERO);
                } else {
                    baseAggregation(shopId, logisticsId, skuInfos, money, logisticsBaseModel, valuationModel);
                }
                break;
            case MONEY:
                //按金额
                if (totalPrice.compareTo(logisticsIncludePostage.getAmountNum().multiply(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND)) == CommonPool.NUMBER_ONE) {
                    money.put(shopId + StrPool.COLON + logisticsId, BigDecimal.ZERO);
                } else {
                    baseAggregation(shopId, logisticsId, skuInfos, money, logisticsBaseModel, valuationModel);
                }
                break;
            case PKGS_MONEY:
                //金额+件数
                if (total > logisticsIncludePostage.getPieceNum() && totalPrice.compareTo(logisticsIncludePostage.getAmountNum().multiply(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND)) == CommonPool.NUMBER_ONE) {
                    money.put(shopId + StrPool.COLON + logisticsId, BigDecimal.ZERO);
                } else {
                    baseAggregation(shopId, logisticsId, skuInfos, money, logisticsBaseModel, valuationModel);
                }
                break;
            case WEIGHT:
                //按重量
                if (totalWeight.compareTo(logisticsIncludePostage.getWeight()) == CommonPool.NUMBER_ONE) {
                    money.put(shopId + StrPool.COLON + logisticsId, BigDecimal.ZERO);
                } else {
                    baseAggregation(shopId, logisticsId, skuInfos, money, logisticsBaseModel, valuationModel);
                }
                break;
            case WEIGHT_MONEY:
                // 重量+金额
                if (totalWeight.compareTo(logisticsIncludePostage.getWeight()) == CommonPool.NUMBER_ONE
                        && totalWeight.compareTo(logisticsIncludePostage.getAmountNum().multiply(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND)) == CommonPool.NUMBER_ONE) {
                    money.put(shopId + StrPool.COLON + logisticsId, BigDecimal.ZERO);
                } else {
                    baseAggregation(shopId, logisticsId, skuInfos, money, logisticsBaseModel, valuationModel);
                }
                break;
            default:
        }
    }

    /**
     * 根据基础模板计算相关运费
     *
     * @param logisticsId    物流模板id
     * @param skuInfos       商品信息
     * @param shopId         商店id
     * @param money          运费金额 <key:(shop:物流模板id) : value(运费金额)>
     * @param valuationModel 计费模式
     */
    private void baseAggregation(Long shopId, Long logisticsId, List<SkuInfoParam> skuInfos, Map<String, BigDecimal> money, LogisticsBaseModel logisticsBaseModel, ValuationModel valuationModel) {
        //按计费方式计算运费
        int total = skuInfos.stream().mapToInt(SkuInfoParam::getNum).sum();
        if (valuationModel == ValuationModel.PKGS) {
            //总件数大于模板首件数 计算超出件数按续费计算  小于首件数按首费计算
            if (total > Integer.parseInt(logisticsBaseModel.getFirstQuantity())) {
                int surplus = total - Integer.parseInt(logisticsBaseModel.getFirstQuantity());
                // 计算剩余件数所需运费  例：剩余件数21件  续件数5件  续费1元 所需运费为 (21 % 5 == 0 ? 21/5 ：21/5 +1)*1
                int number = surplus % Integer.parseInt(logisticsBaseModel.getSecondQuantity()) == CommonPool.NUMBER_ZERO ?
                        surplus / Integer.parseInt(logisticsBaseModel.getSecondQuantity()) :
                        surplus / Integer.parseInt(logisticsBaseModel.getSecondQuantity()) + CommonPool.NUMBER_ONE;
                BigDecimal totalSecondAmount = new BigDecimal(String.valueOf(number));
                // 续费金额加首费金额
                money.put(shopId + StrPool.COLON + logisticsId, totalSecondAmount.multiply(logisticsBaseModel.getSecondAmount()).add(logisticsBaseModel.getFirstAmount()));
            } else {
                money.put(shopId + StrPool.COLON + logisticsId, logisticsBaseModel.getFirstAmount());
            }
        } else if (valuationModel == ValuationModel.WEIGHT) {

            BigDecimal totalWeight = skuInfos.stream().reduce(BigDecimal.ZERO, (list, list1) -> list.add(new BigDecimal(list1.getNum().toString()).multiply(list1.getWeight())), BigDecimal::add);
            if (totalWeight.compareTo(new BigDecimal(logisticsBaseModel.getFirstQuantity())) > CommonPool.NUMBER_ZERO) {
                BigDecimal sub = totalWeight.subtract(new BigDecimal(logisticsBaseModel.getFirstQuantity()));
                // 计算剩余重量所需运费
                BigDecimal number = sub.divide(new BigDecimal(logisticsBaseModel.getSecondQuantity()), CommonPool.NUMBER_ZERO, RoundingMode.CEILING);
                money.put(shopId + StrPool.COLON + logisticsId, number.multiply(logisticsBaseModel.getSecondAmount()).add(logisticsBaseModel.getFirstAmount()));
            } else {
                money.put(shopId + StrPool.COLON + logisticsId, logisticsBaseModel.getFirstAmount());
            }
        }
    }

    /**
     * 新增物流运费模板信息
     *
     * @param logisticsTemplateDto 物流模板dto
     */
    @Override
    public void saveTemplateInfo(LogisticsTemplateDTO logisticsTemplateDto) {
        LogisticsTemplate logisticsTemplate = logisticsTemplateDto.coverLogisticsTemplate();
        boolean save = this.logisticsTemplateService.save(logisticsTemplate);
        if (!save) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED.getCode(), "模版新增失败！");
        }
        //基础模板信息新增
        List<LogisticsBaseModelDTO> logisticsBaseModelDtos = logisticsTemplateDto.getLogisticsBaseModelDTO();
        if (CollUtil.isNotEmpty(logisticsBaseModelDtos)) {
            addLogisticsBaseModel(logisticsBaseModelDtos, logisticsTemplate.getId());
        }
        //模版续费方式新增 先判断是否勾选指定包邮条件
        if (CommonPool.NUMBER_ONE == (logisticsTemplateDto.getChoiceConditionPostage())) {
            List<LogisticsIncludePostageDTO> logisticsIncludePostage = logisticsTemplateDto.getLogisticsIncludePostageDTO();
            if (CollUtil.isNotEmpty(logisticsIncludePostage)) {
                addLogisticsIncludePostage(logisticsIncludePostage, logisticsTemplate.getId());
            }
        }
    }

    /**
     * 修改物流模板信息
     *
     * @param logisticsTemplateDto 物流模板dto
     */
    @Override
    public void updateTemplateInfo(LogisticsTemplateDTO logisticsTemplateDto) {
        LogisticsTemplate logisticsTemplate = logisticsTemplateService.getById(logisticsTemplateDto.getId());
        if (logisticsTemplate == null) {
            throw new GlobalException("当前物流模板不存在");
        }
        BeanUtil.copyProperties(logisticsTemplateDto, logisticsTemplate);
        boolean flag = this.logisticsTemplateService.updateById(logisticsTemplate);
        if (!flag) {
            throw new GlobalException("物流模板修改失败");
        }
        logisticsBaseModelService.lambdaUpdate().eq(LogisticsBaseModel::getLogisticsId, logisticsTemplate.getId()).remove();

        List<LogisticsBaseModelDTO> logisticsBaseModelDtos = logisticsTemplateDto.getLogisticsBaseModelDTO();
        if (CollUtil.isNotEmpty(logisticsBaseModelDtos)) {
            addLogisticsBaseModel(logisticsBaseModelDtos, logisticsTemplate.getId());
        }
        logisticsIncludePostageService.lambdaUpdate().eq(LogisticsIncludePostage::getLogisticsId, logisticsTemplate.getId()).remove();
        //模版续费方式新增 先判断是否勾选指定包邮条件
        if (CommonPool.NUMBER_ONE == (logisticsTemplateDto.getChoiceConditionPostage())) {
            List<LogisticsIncludePostageDTO> logisticsIncludePostageDtos = logisticsTemplateDto.getLogisticsIncludePostageDTO();
            if (CollUtil.isNotEmpty(logisticsIncludePostageDtos)) {
                addLogisticsIncludePostage(logisticsIncludePostageDtos, logisticsTemplate.getId());
            }
        }
    }

    /**
     * 物流模板删除
     *
     * @param id 模板id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTemplateInfo(Long id) {
        LogisticsTemplate logisticsTemplate = logisticsTemplateService.getById(id);
        if (logisticsTemplate == null) {
            throw new GlobalException("当前物流模板不存在");
        }
        // 校验是否有商品在使用该模板
        Boolean flag = goodsRpcService.checkProductByTemplateId(id);
        if (flag) {
            throw new GlobalException("当前模板有商品正在使用,请调整后在删除");
        }
        //删除模板集器关联信息
        this.logisticsIncludePostageService.lambdaUpdate().eq(LogisticsIncludePostage::getLogisticsId, id).remove();
        this.logisticsBaseModelService.lambdaUpdate().eq(LogisticsBaseModel::getLogisticsId, id).remove();
        this.logisticsTemplateService.lambdaUpdate().eq(LogisticsTemplate::getId, id).remove();

    }

    /**
     * 模块续费信息
     *
     * @param logisticsIncludePostageDtos 包邮条件DTO
     * @param id                          物流模板id
     */
    private void addLogisticsIncludePostage(List<LogisticsIncludePostageDTO> logisticsIncludePostageDtos, Long id) {
        logisticsIncludePostageDtos.forEach(bean -> {
            bean.setLogisticsId(id);
            LogisticsIncludePostage logisticsIncludePostage = bean.coverLogisticsIncludePostage();
            boolean save = logisticsIncludePostageService.save(logisticsIncludePostage);
            if (!save) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED.getCode(), "模版续费方式新增失败！");
            }
        });

    }

    /**
     * 模板首费新增
     *
     * @param logisticsBaseModelDtos 基础包邮条件dto
     * @param id                     物流模板id
     */
    private void addLogisticsBaseModel(List<LogisticsBaseModelDTO> logisticsBaseModelDtos, Long id) {
        logisticsBaseModelDtos.forEach(bean -> {
            bean.setLogisticsId(id);
            LogisticsBaseModel logisticsBaseModel = bean.coverLogisticsBaseModel();
            boolean save = logisticsBaseModelService.save(logisticsBaseModel);
            if (!save) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED.getCode(), "模版首费方式新增失败！");
            }
        });
    }
}
