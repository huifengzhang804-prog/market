package com.medusa.gruul.afs.service.service.rpc;

import com.medusa.gruul.afs.api.model.AfsCloseDTO;
import com.medusa.gruul.afs.api.rpc.AfsRpcService;
import com.medusa.gruul.afs.service.service.AfsService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/16
 */
@Service
@DubboService
@RequiredArgsConstructor
public class AfsRpcServiceImpl implements AfsRpcService {

    private final AfsService afsService;

    @Override
    public void closeAfsByAfsNo(AfsCloseDTO afsClose) {
        afsService.afsClose(afsClose);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeAfsBatch(List<AfsCloseDTO> afsCloses) {
        afsCloses.forEach(afsService::afsClose);
    }
}
