package com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.model.vo.ConfVO;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.config.OssMvcConfig;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.OssConstant;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config.LocalStorageConfig;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config.StorageConf;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.dto.OssConfigDto;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.enums.StorageType;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.entity.OssConf;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.mapper.OssConfMapper;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.service.IOssConfService;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import com.medusa.gruul.common.fastjson2.filter.DesensitizeValueFilter;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.validate.ValidateHelper;
import io.vavr.control.Option;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author xiaoq
 * @since 2022-03-24 09:18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OssConfServiceImpl extends ServiceImpl<OssConfMapper, OssConf>
        implements IOssConfService {

    /**
     * todo 解决初始化顺序问题 不使用这个字段 不能删除！！
     */
    @SuppressWarnings("unused")
    private final RedisTemplate<String, Object> redisTemplate;
    private final FileStorageService fileStorageService;


    /**
     * 校验配置对象数据
     *
     * @param conf 对象
     */
    private void validConf(StorageConf conf) {
        Set<ConstraintViolation<StorageConf>> validateErrors = ValidateHelper.validate(conf, Default.class);
        if (CollUtil.isEmpty(validateErrors)) {
            return;
        }
        throw new GlobalException(validateErrors.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(",")));
    }

    /**
     * oss配置编辑  新增or修改
     *
     * @param param oss配置信息
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = OssConstant.OSS_LOCK_KEY, key = "#param.type.name()")
    public void editOssConf(OssConfigDto param) {
        StorageType type = param.getType();
        String desc = param.getDescription();
        param.setType(null);
        param.setDescription(null);
        JSONObject params = JSONObject.from(param);
        StorageConf dbStorageConfig = getStorageConf(type).getOrNull();
        //转成对应类型的配置
        StorageConf conf = params.to(type.getStorageConfClass());

        //手动校验数据
        validConf(conf);
        if (Objects.nonNull(dbStorageConfig)) {
          //脱敏数据判断
          dealDesensitizationData(conf, dbStorageConfig);
        }
        OssConf systemConf = new OssConf()
                .setType(type)
                .setDescription(desc)
                .setConf(JSON.toJSONString(conf));
        //当前编辑的平台
        String currentPlatform = type.name();
        //保存数据 缓存双存/双删
        RedisUtil.doubleDeletion(
                () -> {
                    //尝试更新，更新成功则说明数据存在 否则说明数据不存在
                    boolean updateSuccess = this.lambdaUpdate()
                            .set(OssConf::getConf, systemConf.getConf())
                            .set(OssConf::getDescription, systemConf.getDescription())
                            .eq(OssConf::getType, type)
                            .update();
                    //如果不成功 并且不是删除操作，则说明 是新增
                    if (!updateSuccess) {
                        this.save(systemConf);
                    }
                },
                () -> RedisUtil.executePipelined(
                        operations -> {
                            operations.opsForHash().delete(OssConstant.OSS_KEY,currentPlatform);
                        }
                )
        );
        //如果是本地存储 则设置本地存储路径
        if (conf instanceof LocalStorageConfig config) {
            config.setLocalStoragePath(OssMvcConfig.localOssPath());
        }
        //更新 fileStorageService 配置
        updateCurrentPlatform(type, conf);
    }

  /**
   * 脱敏数据处理
   * @param conf
   * @param dbStorageConfig
   */
  private  void dealDesensitizationData(StorageConf conf, StorageConf dbStorageConfig) {
    Field[] declaredFields = conf.getClass().getDeclaredFields();
    String updateFieldValue;
    String dbFileValue;
    for (Field declaredField : declaredFields) {
      Desensitize annotation = declaredField.getAnnotation(Desensitize.class);
      if (Objects.isNull(annotation)) {
        continue;
      }
      try {
        declaredField.setAccessible(true);
        updateFieldValue= (String) declaredField.get(conf);
        dbFileValue= (String) declaredField.get(dbStorageConfig);
        if (StringUtils.equals(updateFieldValue,
            DesensitizeValueFilter.hide(annotation,dbFileValue))) {
          //如果数据库中字段经过脱敏后处理与前端传递过来的数据一致，说明字段没有发生变化 直接用数据库中的值
          declaredField.set(conf,dbFileValue);
        }
      } catch (IllegalAccessException e) {
        SystemCode.FAILURE.exception(e);
      }

    }
  }


  /**
     * 更新当前的存储配置于存储平台
     *
     * @param type 存储类型
     * @param conf 对应的配置
     */
    private void updateCurrentPlatform(StorageType type, StorageConf conf) {
        //获得存储平台 List
        CopyOnWriteArrayList<FileStorage> storages = fileStorageService.getFileStorageList();
        //获取当前平台配置
        FileStorage currentStorage = fileStorageService.getFileStorage(type.name());
        //如果当前平台配置存在，则移除当前你平台配置 并释放资源
        if (currentStorage != null) {
            //移除当前平台配置
            storages.remove(currentStorage);
            //释放存储资源
            currentStorage.close();
        }
        //添加新的存储配置
        List<? extends FileStorage> newCurrentStorages = type.getConvert().apply(type, conf);
        storages.addAll(newCurrentStorages);
        //设置默认的存储平台为当前平台
        fileStorageService.getProperties().setDefaultPlatform(type.name());
    }

    @Override
    public StorageConf ossConfig(StorageType type) {
        return getStorageConf(type).getOrElse(() -> new StorageConf()) ;

    }

    @Override
    public Map<StorageType, ? extends StorageConf> allConf() {
        //从库种查询数据 redis/db
        Map<StorageType, String> allConfMap = RedisUtil.getCache(
                //从缓存中取数据
                () -> {
                    Map<StorageType, String> cacheMap = RedisUtil.getCacheMap(
                            OssConstant.OSS_KEY,
                            new TypeReference<>() {
                            }
                    );
                    if (MapUtil.isEmpty(cacheMap)) {
                        return null;
                    }
                    return cacheMap;
                },
                //从 db 中取数据
                () -> {
                    Map<StorageType, String> dbMap = this.lambdaQuery()
                            .list()
                            .stream()
                            .collect(
                                    Collectors.toMap(OssConf::getType, OssConf::getConf)
                            );
                    if (MapUtil.isEmpty(dbMap)) {
                        return null;
                    }
                    return dbMap;
                },
                //如果取到数据 放到 redis 种
                (confMap) -> RedisUtil.setCacheMap(OssConstant.OSS_KEY, confMap),
                //取数据存放数据时加的锁
                RedisUtil.key(OssConstant.OSS_LOCK_KEY, "ALL")
        );
        //转成对应对象
        return MapUtil.emptyIfNull(allConfMap)
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(Map.Entry::getKey, entry -> JSON.parseObject(entry.getValue(), entry.getKey().getStorageConfClass()))
                );

    }

    /**
     * 获取当前OSS存储类型对应的配置
     *
     * @param type 存储类型
     * @return 可能为空的配置
     */
    private Option<StorageConf> getStorageConf(StorageType type) {
        return Option.of(
                RedisUtil.getCache(
                        () -> RedisUtil.getCacheMapValue(
                                OssConstant.OSS_KEY,
                                type.name(),
                                type.getStorageConfClass()
                        ),
                        () -> {
                            OssConf one = this.lambdaQuery()
                                    .eq(OssConf::getType, type)
                                    .one();
                            if (one == null) {
                                return null;
                            }
                            StorageConf storageConf = JSON.parseObject(one.getConf(), type.getStorageConfClass());
                            storageConf.setDescription(one.getDescription());
                            return storageConf;
                        },
                        (conf) -> RedisUtil.setCacheMapValue(
                                OssConstant.OSS_KEY,
                                type.name(),
                                conf
                        ),
                        RedisUtil.key(OssConstant.OSS_LOCK_KEY, type.name())
                )
        );
    }

    @Override
    public void ossPlatformInit() {
        //从 redis、数据库种查询 oss 配置
        Map<StorageType, ? extends StorageConf> storageTypeMap = this.allConf();
        if (MapUtil.isEmpty(storageTypeMap)) {
            return;
        }
        //获得存储平台 List
        CopyOnWriteArrayList<FileStorage> storages = fileStorageService.getFileStorageList();
        storageTypeMap.forEach(
                (type, conf) -> {
                    if (conf instanceof LocalStorageConfig config) {
                        config.setLocalStoragePath(OssMvcConfig.localOssPath());
                    }
                    //添加新的存储配置
                    List<? extends FileStorage> newCurrentStorages = type.getConvert().apply(type, conf);
                    storages.addAll(newCurrentStorages);
                }
        );
        String defaultPlatform = RedisUtil.getCacheObject(OssConstant.OSS_USING_KEY);
        log.info("defaultPlatform= {}",defaultPlatform);
        //如果缓存的平台为空则取第一个作为使用种的存储配置的平台
        if (StrUtil.isEmpty(defaultPlatform)) {
            defaultPlatform = storages.get(0).getPlatform();
            RedisUtil.setCacheObject(OssConstant.OSS_USING_KEY, defaultPlatform);
        }
        fileStorageService.getProperties().setDefaultPlatform(defaultPlatform);
    }

    @Override
    public StorageType usingConfig() {
        String platform = RedisUtil.getCacheObject(OssConstant.OSS_USING_KEY);
        return StrUtil.isEmpty(platform) ? null : StorageType.valueOf(platform);
    }

    /**
     * OSS配置列表
     * @return
     */
    @Override
    public List<OssConf> ossList() {
        List<OssConf> list = lambdaQuery()
                .select(OssConf::getId, OssConf::getType, OssConf::getDescription,
                        OssConf::getCreateTime,OssConf::getUpdateTime)
                .list();
        if(CollectionUtil.isEmpty(list)) {
            return list;
        }
        StorageType storageType = usingConfig();
        if (storageType != null) {
            list.forEach(conf -> {
                if (storageType.equals(conf.getType())) {
                    conf.setUsing(true);
                }else {
                    conf.setUsing(false);
                }
            });
        }

        return list;
    }

    @Override
    public void removeConfig(StorageType type) {
        StorageType storageType = usingConfig();
        if (storageType != null && storageType.equals(type)) {
            throw new GlobalException("当前正在使用的存储配置不能删除");
        }
        RedisUtil.doubleDeletion(()->{
            lambdaUpdate()
                    .eq(OssConf::getType, type)
                    .remove();
        },()->{
            RedisUtil.delCacheMapValue( OssConstant.OSS_KEY, type.name());
        });


    }

    /**
     *设置启用的服务商类型
     * @param type
     */
    @Override
    public void using(StorageType type) {
        RedisUtil.setCacheObject(OssConstant.OSS_USING_KEY, type.name());
        StorageConf conf = getStorageConf(type).getOrNull();
        if (Objects.isNull(conf)) {
            throw new GlobalException("未配置该存储类型");
        }
        //如果是本地存储 则设置本地存储路径
        if (conf instanceof LocalStorageConfig config) {
            config.setLocalStoragePath(OssMvcConfig.localOssPath());
        }
        //更新 fileStorageService 配置
        updateCurrentPlatform(type, conf);

    }
}
