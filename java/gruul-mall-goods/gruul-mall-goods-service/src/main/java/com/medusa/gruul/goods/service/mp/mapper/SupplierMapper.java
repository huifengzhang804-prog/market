package com.medusa.gruul.goods.service.mp.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.goods.api.entity.Supplier;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.goods.service.model.param.SupplierParam;
import com.medusa.gruul.goods.service.model.vo.SupplierVO;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 供应商 Mapper 接口
 *
 * @author xiaoq
 * @since 2022-03-04
 */
public interface SupplierMapper extends BaseMapper<Supplier> {

    /**
     * 获取供应商列表
     *
     * @param supplierParam 查询条件
     * @return 供应商list信息
     */
    IPage<SupplierVO> querySupplierList(@Param("supplierParam") SupplierParam supplierParam);


}
