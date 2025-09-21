package com.medusa.gruul.afs.api.rpc;

import com.medusa.gruul.afs.api.model.AfsCloseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/16
 */
public interface AfsRpcService {

    /**
     * 买家操作订单导致的售后工单号关闭
     *
     * @param afsClose 售后工单号 与 关闭原因
     */
    void closeAfsByAfsNo(@Valid AfsCloseDTO afsClose);


    /**
     * 批量关闭售后
     *
     * @param afsCloses 关闭详情
     */
    void closeAfsBatch(@NotNull @Size(min = 1) List<AfsCloseDTO> afsCloses);
}
