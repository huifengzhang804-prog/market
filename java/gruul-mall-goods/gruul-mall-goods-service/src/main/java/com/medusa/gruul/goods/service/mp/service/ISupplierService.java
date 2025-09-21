package com.medusa.gruul.goods.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.model.dto.SupplierDTO;
import com.medusa.gruul.goods.service.model.param.SupplierParam;
import com.medusa.gruul.goods.service.model.param.SupplierProductParam;
import com.medusa.gruul.goods.service.model.vo.SupplierVO;

/**
 *
 * 供应商 服务类
 *
 *
 * @author xiaoq
 * @since 2022-03-04
 */
public interface ISupplierService extends IService<Supplier> {

    /**
     * 删除供应商
     *
     * @param ids 供应商ids
     */
    void deleteSupplierList(Long[] ids);

    /**
     * 新增 供应商
     * @param supplierDto 供应商dto
     */
    void addSupplier(SupplierDTO supplierDto);

    /**
     * 修改供应商
     * @param supplierDto 供应商dto
     */
    void updateSupplier(SupplierDTO supplierDto);

    /**
     * 供应商分页列表
     *
     * @param supplierParam 查询条件
     * @return 分页数据
     */
    IPage<SupplierVO> getSupplierList(SupplierParam supplierParam);

    /**
     * 供应商审核
     * @param supplierDto 供应商dto
     */
    void checkSupplier(SupplierDTO supplierDto);

    /**
     * 获取供应商下商品基础信息
     *
     * @param supplierProductParam 供应商产品查询参数
     * @return    IPage<商品基础数据>
     */
    IPage<Product> getSupplierProductList(SupplierProductParam supplierProductParam);
}
