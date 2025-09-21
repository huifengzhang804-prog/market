package com.medusa.gruul.overview.service.modules.export.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.model.DataExportRecordQueryDTO;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据导出记录Controller
 *
 * @author jipeng
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("""
        		@S.matcher()
        		.any(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN,@S.PLATFORM_ADMIN,
        		@S.SHOP_ADMIN, @S.SHOP_CUSTOM_ADMIN, @S.R.SHOP_STORE,
        		@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
        		.or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS, 'order'))
        		.or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'order:delivery'))
        		.or(@S.consumer().eq(@S.ROLES, @S.USER))
        		.match()
        """)
@RequestMapping("export")
@Slf4j
public class DataExportRecordController {

    private final IDataExportRecordManageService dataExportRecordManageService;

    // 存储每个导出订单对应的SSE链接
    private ConcurrentHashMap<Long, List<SseEmitter>> clients = new ConcurrentHashMap<>();

    // SSE 连接订阅
    @GetMapping(value = "/sse/{id}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("id") Integer fileId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        List<SseEmitter> sseEmitters = clients.getOrDefault(fileId, Lists.newArrayList());
        sseEmitters.add(emitter);

        emitter.onCompletion(() -> {
            clients.remove(fileId);
        });
        emitter.onError((throwable)->{
            clients.remove(fileId);
        });
        emitter.onTimeout(() -> {
            clients.remove(fileId);
        });
        // 可以通过 emitter.send() 向用户推送消息
        return emitter;
    }

    /**
     * 删除数据导出记录
     *
     * @param id 数据导出记录id
     * @return 删除数据导出记录操作是否成功 成功返回true  失败返回false
     */
    @Idem(500)
    @DeleteMapping("/{id}/remove")
    @Log("删除数据导出记录")
    public Result<Boolean> deleteDataExportRecord(@PathVariable("id") Long id) {

        dataExportRecordManageService.removeById(id);

        return Result.ok(Boolean.TRUE);
    }

    /**
     * 批量删除数据导出记录
     *
     * @param map 删除导出记录的ids
     * @return 删除状态成功与否
     */
    @Idem(500)
    @DeleteMapping("/batchRemove")
    @Log("批量删除数据导出记录")
    public Result<Boolean> batchDeleteDataExportRecord(@RequestBody Map<String, List<Long>> map) {

        return Result.ok(dataExportRecordManageService.batchRemove(map.get("ids")));
    }

    /**
     * 下载数据导出记录
     *
     * @param id       导出记录id
     * @param response 响应流
     */
    @Idem(500)
    @GetMapping("/{id}/download")
    @Log("下载数据导出记录")
    public void downloadDataExportRecord(@PathVariable("id") Long id,
                                         HttpServletResponse response) {
        dataExportRecordManageService.downloadDataExportRecord(id, response);

    }

    /**
     * 列表查询数据导出记录
     *
     * @param dataExportRecordQueryDTO 查询条件
     * @return 分页数据
     */
    @Idem(500)
    @GetMapping("/list")
    @Log("查询数据导出记录")
    public Result<Page<DataExportRecordDTO>> list(DataExportRecordQueryDTO dataExportRecordQueryDTO) {
        return Result.ok(dataExportRecordManageService.list(dataExportRecordQueryDTO));
    }

    /**
     * 查询正在导出中的记录条数
     *
     * @return 正在导出中的记录条数
     */
    @Idem(500)
    @GetMapping("/count")
    @Log("查询正在导出中的记录条数")
    public Result<Long> count() {
        return Result.ok(dataExportRecordManageService.generateCount());
    }

    public ConcurrentHashMap<Long, List<SseEmitter>> getClients() {
        return clients;
    }
}
