package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.service.model.dto.UserBalanceHistoryDTO;
import com.medusa.gruul.user.service.model.vo.UserBalanceHistoryQueryVo;
import com.medusa.gruul.user.service.service.IUserBalanceHistoryManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户余额历史 控制层
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.controller
 * @author:jipeng
 * @createTime:2024/1/19 16:40
 * @version:1.0
 */
@RestController
@RequestMapping("/user/balance/")
@RequiredArgsConstructor
public class UserBalanceHistoryController {


  private final IUserBalanceHistoryManageService userBalanceHistoryManageService;

  /**
   * 用户储值流水列表查询
   *
   * @param queryVo query
   * @return 用户储值流水列表
   */
  @Log("用户储值流水列表查询")
  @Idem(200)
  @GetMapping("/list")
  public Result<Page<UserBalanceHistoryDTO>> queryList(
      UserBalanceHistoryQueryVo queryVo) {
    Page<UserBalanceHistoryDTO> result = userBalanceHistoryManageService.list(queryVo);

    return Result.ok(result);
  }

  /**
   * 储值流水备注
   *
   * @param dto 备注信息
   * @return 备注成功
   */
  @Log("储值流水备注")
  @Idem(200)
  @PostMapping("/remark")
  public Result<Boolean> remark(@RequestBody UserBalanceHistoryDTO dto){
    userBalanceHistoryManageService.remark(dto.getIds(),dto.getRemark());
    return Result.ok(Boolean.TRUE);
  }

  /**
   * 储值流水导出
   * @param queryVo query
   * @return 导出id
   */
  @Log("储值流水导出")
  @Idem(200)
  @PostMapping("/export")
  public Result<Long> export(@RequestBody UserBalanceHistoryQueryVo queryVo){
    Long exportId=userBalanceHistoryManageService.export(queryVo);
    return Result.ok(exportId);
  }

}
