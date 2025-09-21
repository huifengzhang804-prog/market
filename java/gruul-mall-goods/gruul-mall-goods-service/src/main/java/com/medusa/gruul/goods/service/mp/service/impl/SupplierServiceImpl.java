package com.medusa.gruul.goods.service.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.Supplier;
import com.medusa.gruul.goods.api.entity.SupplierRateRecord;
import com.medusa.gruul.goods.api.model.dto.SupplierDTO;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.SupplierStatus;
import com.medusa.gruul.goods.service.model.param.SupplierParam;
import com.medusa.gruul.goods.service.model.param.SupplierProductParam;
import com.medusa.gruul.goods.service.model.vo.SupplierVO;
import com.medusa.gruul.goods.service.mp.mapper.ProductMapper;
import com.medusa.gruul.goods.service.mp.mapper.SupplierMapper;
import com.medusa.gruul.goods.service.mp.mapper.SupplierRateRecordMapper;
import com.medusa.gruul.goods.service.mp.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 供应商 服务实现类
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements ISupplierService {

    private final ProductMapper productMapper;
    private final SupplierMapper supplierMapper;
    private final SupplierRateRecordMapper supplierRateRecordMapper;

    /**
     * 删除供应商
     *
     * @param ids 供应商ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSupplierList(Long[] ids) {
        new LambdaUpdateChainWrapper<>(productMapper)
                .in(Product::getProviderId, Arrays.asList(ids))
                .set(Product::getStatus, ProductStatus.SELL_OFF.getStatus())
                .set(Product::getProviderId, null).update();
        //删除供应商
        baseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 新增 供应商
     *
     * @param supplierDto 供应商dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSupplier(SupplierDTO supplierDto) {
        //判断供应商手机号是否存在
        List<Supplier> sameSuppliers = baseMapper.selectList(new QueryWrapper<Supplier>()
                .eq("mobile", supplierDto.getMobile()).orderByDesc("create_time"));

        ifExistSupplierMobile(sameSuppliers);

        //状态默认已审核
        supplierDto.setStatus(SupplierStatus.REVIEW);
        //评分默认5.0
        if (supplierDto.getScore() == null) {
            supplierDto.setScore(BigDecimal.valueOf(5.0));
        }

        //供应商识别号生成
        String supplierSn;
        Supplier supplierSearch ;
        do {
            //生成一个16位的供货商识别号
            String date = DateUtil.format(new Date(), new SimpleDateFormat("yyyyMMdd"));
            long newCount = count.incrementAndGet();
            String newCountStr = String.format("%06d", newCount);
            supplierSn = "GY" + date + newCountStr;
            //验证改id是否已经被使用
            supplierSearch = this.baseMapper.selectOne(new QueryWrapper<Supplier>().eq("supplier_sn", supplierSn));

        } while (supplierSearch != null);
        supplierDto.setSupplierSn(supplierSn);
        //新增
        Supplier supplier = supplierDto.coverSupplier();
        int insert = supplierMapper.insert(supplier);
        GoodsError.SUPPLIER_ADD_FAIL.trueThrow(insert == 0);
    }

    private final AtomicLong count = new AtomicLong();

    /**
     * 修改供应商
     *
     * @param supplierDto 供应商dto
     */
    @Override
    public void updateSupplier(SupplierDTO supplierDto) {
        //判断原供应商是否已被删除
        Supplier oldSupplier = supplierMapper.selectById(supplierDto.getId());
        filterDetection(oldSupplier);
        //更新
        Supplier supplier = supplierDto.coverSupplier();
        int update = supplierMapper.updateById(supplier);
        GoodsError.SUPPLIER_UPDATE_FAIL.trueThrow(update < CommonPool.NUMBER_ONE);
    }

    /**
     * 供应商分页列表
     *
     * @param supplierParam 查询数据
     * @return 供应商分页对象
     */
    @Override
    public IPage<SupplierVO> getSupplierList(SupplierParam supplierParam) {
        IPage<SupplierVO> supplierPage = baseMapper.querySupplierList(supplierParam);
        List<SupplierVO> records = supplierPage.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            Set<Long> supperIdList = records.stream().map(SupplierVO::getId).collect(Collectors.toSet());
            List<SupplierRateRecord> supplierRateRecordList = supplierRateRecordMapper.querySupplierRealRate(supperIdList);
            if (CollUtil.isEmpty(supplierRateRecordList)) {
                return supplierPage;
            }
            Map<Long, SupplierRateRecord> collect = supplierRateRecordList.stream().collect(Collectors.toMap(SupplierRateRecord::getSupplierId, supplierRateRecord -> supplierRateRecord));
            records.forEach(bean -> {
                SupplierRateRecord supplierRateRecord = collect.get(bean.getId());
                if (supplierRateRecord == null) {
                    return;
                }
                BigDecimal rate = new BigDecimal(supplierRateRecord.getRate().toString());
                BigDecimal evaluateNum = new BigDecimal(supplierRateRecord.getEvaluateNum().toString());
                BigDecimal supplierRate = rate.divide(evaluateNum, CommonPool.NUMBER_ONE, RoundingMode.HALF_EVEN);
                bean.setScore(supplierRate);
            });
        }
        return supplierPage;
    }

    /**
     * 供应商审核
     *
     * @param supplierDto 供应商dto
     */
    @Override
    public void checkSupplier(SupplierDTO supplierDto) {
        //判断原供应商是否已被删除
        Supplier oldSupplier = baseMapper.selectById(supplierDto.getId());
        filterDetection(oldSupplier);
        //更新
        BeanUtil.copyProperties(supplierDto, oldSupplier);
        int update = baseMapper.updateById(oldSupplier);
        GoodsError.SUPPLIER_AUDIT_FAIL.trueThrow(update < CommonPool.NUMBER_ONE);
    }

    @Override
    public IPage<Product> getSupplierProductList(SupplierProductParam supplierProductParam) {
        return productMapper.getSupplierProductList(supplierProductParam);
    }


    /**
     * 判断供应商手机号是否已存在
     *
     * @param sameSuppliers 供应商信息
     */
    private void ifExistSupplierMobile(List<Supplier> sameSuppliers) {
        if (CollUtil.isEmpty(sameSuppliers)) {
            return;
        }
        Supplier supplier = sameSuppliers.get(CommonPool.NUMBER_ZERO);
        if (supplier == null) {
            return;
        }
        switch (supplier.getStatus()) {
            case REVIEW -> throw GoodsError.PHONE_ALREADY_SUPPLIER.exception();
            case FORBIDDEN -> throw GoodsError.PHONE_ALREADY_SUPPLIER_BUT_FORBIDDEN.exception();
        }
    }

    private void filterDetection(Supplier oldSupplier) {
        GoodsError.SUPPLIER_HAVE_DELETE.trueThrow(oldSupplier == null);
        //判断供应商手机号是否存在（排除自己）
        assert oldSupplier != null;
        List<Supplier> sameSuppliers = baseMapper.selectList(new QueryWrapper<Supplier>()
                .eq("mobile", oldSupplier.getMobile())
                .ne("id", oldSupplier.getId())
        );
        ifExistSupplierMobile(sameSuppliers);
    }

}
