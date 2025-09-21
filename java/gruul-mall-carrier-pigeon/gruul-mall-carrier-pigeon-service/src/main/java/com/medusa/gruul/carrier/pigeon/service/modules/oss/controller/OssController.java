package com.medusa.gruul.carrier.pigeon.service.modules.oss.controller;

import com.medusa.gruul.carrier.pigeon.service.model.vo.ConfVO;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.FileHelper;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config.StorageConf;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.dto.OssConfigDto;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.enums.StorageType;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.entity.OssConf;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.service.IOssConfService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;


/**
 * oss配置控制器
 *
 * @author xiaoq
 * @since 2022-03-07 13:41
 */
@Slf4j
@RestController
@RequestMapping("/oss")
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('generalSet')")
public class OssController {
    private final IOssConfService systemConfService;
    private final FileStorageService fileStorageService;


    /**
     * 保存或更新oss配置 当前配置会作为默认使用的 OSS 平台
     *
     * @param editOssConfigDto oss配置信息
     */
    @PostMapping("edit")
    public Result<Void> editOssConf(@RequestBody @Valid OssConfigDto editOssConfigDto) {
        systemConfService.editOssConf(editOssConfigDto);
        return Result.ok();
    }

    @Log("oss配置列表查询")
    @GetMapping("list")
    public Result<List<OssConf>> list(){
        List<OssConf> ossList =systemConfService.ossList();
        return Result.ok(ossList);
    }

    @Log("删除指定服务商的配置")
    @DeleteMapping("/remove/{type}")
    public Result<Void> remove(@PathVariable(name = "type") StorageType type){
        systemConfService.removeConfig(type);
        return Result.ok();
    }

    @Log("设置当前使用的服务商")
    @PutMapping("/using/{type}")
    @Idem(1000)
    public Result<Void> using(@PathVariable(name = "type") StorageType type){
        systemConfService.using(type);
        return Result.ok();
    }



    /**
     * 根据类型获取oss配置
     *
     * @param type 查询类型 0-当前使用的配置 1：七牛  2：阿里云  3：腾讯云
     * @return OssConfigDto
     */
    @Log("获取指定的的服务商配置")
    @GetMapping("current/config/{type}")
    public Result<StorageConf> ossConfig(@PathVariable(name = "type") StorageType type) {
        return Result.ok(
                systemConfService.ossConfig(type)
        );
    }


    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @PreAuthorize("@S.authenticated")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        StorageType storageType = systemConfService.usingConfig();
        if (Objects.isNull(storageType)) {
            //检测是否配置上传组件参数
            throw new GlobalException("平台端OSS参数未配置或未启用");
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        FileInfo upload = FileHelper.uploadWithPath(fileStorageService.of(file));
        stopWatch.stop();
        log.debug("上传使用时间:{}毫秒", stopWatch.getLastTaskTimeMillis());
        return Result.ok(upload.getUrl(), upload.getOriginalFilename());
    }


}
