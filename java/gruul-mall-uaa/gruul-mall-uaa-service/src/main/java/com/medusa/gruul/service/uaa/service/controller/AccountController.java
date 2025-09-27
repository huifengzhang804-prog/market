package com.medusa.gruul.service.uaa.service.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;

import com.medusa.gruul.service.uaa.service.model.dto.SalesSectionNoPageDTO;
import com.medusa.gruul.service.uaa.service.model.dto.SalesSectionPageDTO;
import com.medusa.gruul.service.uaa.service.model.dto.account.Account;
import com.medusa.gruul.service.uaa.service.model.dto.account.AccountDTO;
import com.medusa.gruul.service.uaa.service.model.vo.ProductVO;
import com.medusa.gruul.service.uaa.service.model.vo.SalesSectionListVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserPointVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;
import com.medusa.gruul.service.uaa.service.service.AccountService;
import com.medusa.gruul.service.uaa.service.service.SalesSectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * 会员前端控制器
 *
 * @author WuDi
 * @since 2022-09-13
 */
@Slf4j
@RestController
@JsonInclude(JsonInclude.Include.ALWAYS)
@RequiredArgsConstructor
@RequestMapping("/auc")
public class AccountController {


    private final AccountService service;
    private final UaaRpcService uaaRpcService;
    private final SalesSectionService salesSectionService;

    /**
     * 分页查询会员信息
     *
     * @param accountDTO 会员查询参数
     * @return 会员列表
     */
    @PostMapping("register")
    @Log("register")
    public Result<Map> register(@RequestBody AccountDTO accountDTO) {
        // 验证码校验
        uaaRpcService.checkSmsCodeByType(SmsType.LOGIN,accountDTO.getPhone(),accountDTO.getVerificationCode());


        Account account = new Account();
        BeanUtils.copyProperties(accountDTO, account);
        account.setNumUserId(generate9DigitId());
        account.setNickName(accountDTO.getUsername());
        //校验用户名是否重复/校验同一手机号用户数量是否超过五个
        int accountNum=service.countAccountByUserName(account);
        log.info("accountNum:{}",accountNum);
        if(accountNum>0){
            return Result.failed("用户已存在");
        }

        accountNum=service.countAccountByMobile(account);
        log.info("accountNum:{}",accountNum);
        if(accountNum>=5){
            return Result.failed("用户数量超过五个");
        }
        //新增用户
        service.insertNewAccount(account);
        //todo set token

        Map map = new HashMap();
        map.put("user",account);
        map.put("token","");

        return Result.ok(map);
    }


    @PostMapping("login")
    @Log("login")
    public Result<Map> login(@RequestBody AccountDTO accountDTO) {
        Account result=service.getAccountByAccAndPwd(accountDTO);


        if(BeanUtil.isEmpty(result)){
            return Result.failed("用户名或密码错误");
        }
        result.setType("4");
        //todo set token
        Map map = new HashMap();
        map.put("user",result);
        map.put("token",service.getToken(accountDTO,true));
        return Result.ok(map);
    }


    @PostMapping("getUserListByPhone")
    @Log("getUserListByPhone")
    public Result<Map> getUserListByPhone(@RequestBody AccountDTO accountDTO) {
        // 验证码校验
        uaaRpcService.checkSmsCodeByType(SmsType.LOGIN,accountDTO.getPhone(),accountDTO.getVerificationCode());

        List<Account> userList=service.getUserListByPhone(accountDTO);
        Map map = new HashMap();
        map.put("userList",userList);
        return Result.ok(map);
    }


    @PostMapping("loginUserId")
    @Log("loginUserId")
    public Result<Map> loginUserId(@RequestBody AccountDTO accountDTO) {

        Account account=service.getUserByUserId(accountDTO);
        BeanUtil.copyProperties(account,accountDTO);
        //todo set token
        Map map = new HashMap();
        map.put("user",account);
        map.put("token", service.getToken(accountDTO,false));
        return Result.ok(map);
    }


    @PostMapping("resetPassword")
    @Log("loginUserId")
    public Result<String> resetPassword(@RequestBody AccountDTO accountDTO) {
        try{
            service.resetPassword(accountDTO);
        }catch (Exception e){
            return Result.failed("重置密码失败");
        }

        //todo set token
        return Result.ok("重置密码成功");
    }

    @PostMapping("getVerifyCode")
    @Log("getVerifyCode")
    public Result<String> getVerifyCode(@RequestBody AccountDTO accountDTO) {
        try{
           String verifyCode= uaaRpcService.getVerifyCode(accountDTO.getPhone());
           log.info("verifyCode:{}",verifyCode);
//            service.resetPassword(accountDTO);
        }catch (Exception e){
            return Result.failed("验证码发送失败");
        }

        //todo set token
        return Result.ok("验证码发送成功");
    }


    @PostMapping("getSalesSectionList")
    @Log("getSalesSectionList")
    public Result<IPage<ProductVO>> getSalesSectionList(@RequestBody SalesSectionPageDTO dto) {

        IPage<ProductVO> salesSectionList= salesSectionService.getSalesSectionList(dto);

        return Result.ok(salesSectionList);
    }

    @GetMapping("getUserPointsInfo")
    @Log("getUserPointsInfo")
    public Result<List<UserPointVO>> getUserPointsInfo() {
        Long userId=ISecurity.secureUser().getId();
        log.info("当前登录用户的userId:{}",userId);
        List<UserPointVO> userPointVOList= salesSectionService.getUserPointsInfo(userId);

        return Result.ok(userPointVOList);
    }

    @PostMapping("getSectionName")
    @Log("getSectionName")
    public Result<String> getSectionName(@RequestBody SalesSectionNoPageDTO dto) {

        String sectionName= salesSectionService.getSectionName(dto);

        return Result.ok(sectionName);
    }

    public static long generate9DigitId() {
        UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        // 取绝对值并限制在9位数范围内
        long id = Math.abs(mostSignificantBits) % 1000000000L;
        // 确保是9位数（不小于100000000）
        if (id < 100000000L) {
            id += 100000000L;
        }
        return id;
    }





}

