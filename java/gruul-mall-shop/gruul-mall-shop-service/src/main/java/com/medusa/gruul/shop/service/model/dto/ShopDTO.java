package com.medusa.gruul.shop.service.model.dto;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.constant.RegexPools;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.constant.ShopConstant;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.entity.ShopBankAccount;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.medusa.gruul.shop.api.enums.ShopSettledWayEnum;
import com.medusa.gruul.shop.api.model.dto.ShopExtraDTO;
import com.medusa.gruul.shop.api.model.dto.SigningCategoryDTO;
import com.medusa.gruul.shop.service.model.enums.ShopError;
import com.medusa.gruul.shop.service.mp.entity.ShopRegisterInfo;
import com.medusa.gruul.shop.service.mp.service.IShopBankAccountService;
import com.medusa.gruul.shop.service.mp.service.IShopRegisterInfoService;
import com.medusa.gruul.shop.service.mp.service.IShopService;
import com.vividsolutions.jts.geom.Point;
import io.vavr.control.Option;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author 张治保 date 2022/4/14
 */
@Getter
@Setter
@ToString
public class ShopDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺运行模式
     */
    @NotNull
    private ShopMode shopMode;

    /**
     * 店铺公司名称
     */
    @NotBlank
    private String companyName;

    /**
     * 店铺名称
     */
    @NotBlank
    private String name;

    /**
     * 联系电话 手机号或座机号
     */
    @NotBlank
    @Pattern(regexp = RegexPools.MOBILE_TEL, message = "请输入正确的手机号")
    private String contractNumber;

    /**
     * logo url
     */
    @NotBlank
    @Pattern(regexp = RegexPool.URL_HTTP, message = "图片地址URL不正确")
    private String logo;

    /**
     * 用户id
     */
    private Long userId;


    private String userPhone;

    /**
     * 定位
     */
    @NotNull
    private Point location;

    /**
     * 省市区
     */
    @NotNull
    @Size(min = 2, max = 3, message = "请选择省市区")
    private List<String> area;

    /**
     * 详细地址
     */
    @NotBlank
    private String address;

    /**
     * 介绍
     */
    @NotBlank
    @Size(min = 2, max = 200)
    private String briefing;

    /**
     * 店铺司法注册信息
     */
    @Valid
    private ShopRegisterInfoDTO registerInfo;

    /**
     * 银行账户信息
     */
    @Valid
    @NotNull
    private ShopBankAccountDTO bankAccount;

    /**
     * 签约类目
     */
    private List<SigningCategoryDTO> signingCategory;


    /**
     * 店铺类型
     */
    private ShopType shopType;


    /**
     * 抽佣类型
     */
    private ExtractionType extractionType;

    /**
     * 抽成百分比
     */
    private Integer drawPercentage;


    @SneakyThrows
    public void validParam(boolean isPlatform, Long userId, UaaRpcService uaaRpcService) {
        if (!isPlatform) {
            // 不是从平台端增的店铺 用当前登录的用户ID作为店铺的用户id
            setUserId(userId);
            setUserPhone(ISecurity.userMust().getMobile());
        } else {
            //系统添加指定的用户ID
            Long signUserId = getUserId();
            SystemCode.PARAM_VALID_ERROR.trueThrow(signUserId == null);
            Option<UserInfoVO> option = uaaRpcService.getUserDataByUserId(signUserId);
            UserInfoVO userinfoVo = option.getOrElseThrow((Supplier<Throwable>) () -> new GlobalException(ShopError.SHOP_SIGNED_ADMIN_NOT_FOUND));
            setUserPhone(userinfoVo.getMobile());

        }
    }

    /**
     * 保存商家注册基础信息
     *
     * @param isEdit        是否是编辑模式
     * @param shopService   店铺持久化服务
     * @param shopId        店铺id 编辑模式必填
     * @param uaaRpcService
     * @return 基础信息id
     */
    public Shop saveShop(boolean isEdit, boolean isPlatform, IShopService shopService, Long shopId, Long userId, UaaRpcService uaaRpcService) {
        this.validParam(isPlatform, userId, uaaRpcService);
        boolean exists = shopService.lambdaQuery()
                .eq(Shop::getName, getName())
                .ne(isEdit, Shop::getId, shopId)
                .exists();
        ShopError.SHOP_NAME_EXISTED.trueThrow(exists);
        Shop shop = null;
        SystemCode.DATA_NOT_EXIST.trueThrow(isEdit && (shop = shopService.getById(shopId)) == null);
        shop = toShop(shop, isPlatform);
        boolean success = isEdit ? shopService.updateById(shop) : shopService.save(shop);
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        return shop;
    }

    /**
     * @param isEdit                  是否是编辑模式
     * @param shopRegisterInfoService 持久化服务 保存商家司法注册信息
     */
    public void saveShopRegisterInfo(boolean isEdit, IShopRegisterInfoService shopRegisterInfoService) {
        ShopRegisterInfo shopRegisterInfo = new ShopRegisterInfo();
        if (isEdit) {
            shopRegisterInfo = shopRegisterInfoService.lambdaQuery().one();
            shopRegisterInfo = shopRegisterInfo == null ? new ShopRegisterInfo() : shopRegisterInfo;
        }
        shopRegisterInfo
                .setLicense(registerInfo.getLicense())
                .setLegalPersonIdFront(registerInfo.getLegalPersonIdFront())
                .setLegalPersonIdBack(registerInfo.getLegalPersonIdBack());

        boolean success = shopRegisterInfo.getId() != null ? shopRegisterInfoService.updateById(shopRegisterInfo)
                : shopRegisterInfoService.save(shopRegisterInfo);
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
    }

    /**
     * @param isEdit                 是否是编辑模式
     * @param shopBankAccountService 持久化服务 保存商家银行账户信息
     */
    public void saveShopBankAccount(boolean isEdit, IShopBankAccountService shopBankAccountService) {
        ShopBankAccount shopBankAccount = new ShopBankAccount();
        if (isEdit) {
            shopBankAccount = shopBankAccountService.lambdaQuery().one();
            SystemCode.DATA_NOT_EXIST.trueThrow(shopBankAccount == null);
        }
        shopBankAccount
                .setBankAccount(bankAccount.getBankAccount())
                .setBankName(bankAccount.getBankName())
                .setOpenAccountBank(bankAccount.getOpenAccountBank())
                .setPayee(bankAccount.getPayee());
        boolean success = isEdit ? shopBankAccountService.updateById(shopBankAccount)
                : shopBankAccountService.save(shopBankAccount);
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
    }


    private Shop toShop(Shop shop, boolean isPlatform) {
        Shop newShop = shop == null ? new Shop() : shop;
        boolean isEdit = newShop.getId() != null;

        //平台端增加的店铺无需审核 审核通过时间自动为当前时间
        ShopExtraDTO extraDTO = null;
        if (!isEdit) {
            //只有新增的时候 才填充这些数据
            if (isPlatform) {
                //添加入驻
                extraDTO = new ShopExtraDTO()
                        .setSettledWay(ShopSettledWayEnum.ADD)
                        .setOperatorUserId(ISecurity.userMust().getId())
                        .setOperatorName(ISecurity.userMust().getNickname())
                        .setOperatorPhone(ISecurity.userMust().getMobile())
                        .setAuditTime(LocalDateTime.now());
            } else {
                //申请入驻
                extraDTO = new ShopExtraDTO()
                        .setSettledWay(ShopSettledWayEnum.APPLY);
            }
        }

        return newShop.setNo(
                        isEdit ? newShop.getNo() : RedisUtil.no(ShopConstant.SHOP_NO_KEY_HEAD, ShopConstant.SHOP_NO_KEY))
                .setCompanyName(getCompanyName())
                .setUserId(getUserId())
                .setUserPhone(getUserPhone())
                .setName(getName())
                .setContractNumber(getContractNumber())
                .setLocation(getLocation())
                .setArea(getArea())
                .setAddress(getAddress())
                .setLogo(getLogo())
                .setBriefing(getBriefing())
                .setShopType((getShopType() == null && !isPlatform) ? ShopType.ORDINARY : getShopType())
                .setExtractionType((getExtractionType() == null && !isPlatform) ? ExtractionType.CATEGORY_EXTRACTION
                        : getExtractionType())
                .setDrawPercentage((getExtractionType() == ExtractionType.ORDER_SALES_EXTRACTION && isPlatform)
                        ? getDrawPercentage() : null)
                .setShopMode(newShop.getStatus() == ShopStatus.UNDER_REVIEW ? getShopMode()
                        : newShop.getShopMode() == null ? getShopMode() : newShop.getShopMode())
                .setExtra(extraDTO)
                //在售商品数量
                .setOnSaleGoodsCount(isEdit ? newShop.getOnSaleGoodsCount() : 0)
                //平台端增加的店铺不需要审核 自动通过
                .setStatus(isEdit ? newShop.getStatus() : (isPlatform ? ShopStatus.NORMAL : ShopStatus.UNDER_REVIEW));
    }


}
