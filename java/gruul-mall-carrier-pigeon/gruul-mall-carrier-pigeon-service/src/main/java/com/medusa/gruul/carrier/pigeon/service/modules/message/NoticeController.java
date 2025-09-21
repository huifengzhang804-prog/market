package com.medusa.gruul.carrier.pigeon.service.modules.message;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticeDTO;
import com.medusa.gruul.carrier.pigeon.service.model.dto.NoticePageDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.PigeonNoticeVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.PigeonMessage;
import com.medusa.gruul.carrier.pigeon.service.service.NoticeService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 消息控制器
 *
 * @author 张治保
 * date 2022/4/26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pigeon/notice")
@PreAuthorize("@S.platformPerm('news')")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 分页查询消息提醒
     *
     * @param noticePage 分页参数
     * @return 查询结果
     */
    @GetMapping("/platform")
    @Log("平台端分页查询公告消息列表")
    public Result<IPage<PigeonMessage>> pageNoticePlatform(NoticePageDTO noticePage) {
        return Result.ok(
                noticeService.pageNoticePlatform(noticePage)
        );
    }


    /**
     * 商家端分页查询消息提醒
     *
     * @param noticePage 分页参数
     * @return 查询结果
     */
    @GetMapping
    @Log("商家端查询消息通知")
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN).match()")
    public Result<IPage<PigeonNoticeVO>> pageNotice(NoticePageDTO noticePage) {
        return Result.ok(
                noticeService.pageNotice(noticePage)
        );
    }

    /**
     * 查询消息详情
     *
     * @param noticeId 消息id
     * @return 消息详情
     */
    @GetMapping("/{noticeId}")
    @Log("商家端查看公告详情")
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN).match()")
    public Result<PigeonNoticeVO> noticeById(@PathVariable Long noticeId) {
        return Result.ok(
                noticeService.noticeById(noticeId)
        );
    }

    /**
     * 新增公告
     *
     * @param notice 消息数据
     */
    @PostMapping
    @Log("新增公告")
    public Result<Void> newNotice(@RequestBody @Valid NoticeDTO notice) {
        noticeService.newNotice(notice);
        return Result.ok();
    }

    /**
     * 编辑消息通知
     *
     * @param noticeId 消息id
     * @param notice   消息数据
     */
    @PutMapping("/{noticeId}")
    @Log("编辑消息通知")
    public Result<Void> editNotice(@PathVariable Long noticeId, @RequestBody @Valid NoticeDTO notice) {
        noticeService.editNotice(noticeId, notice);
        return Result.ok();
    }

    /**
     * 推送消息
     *
     * @param noticeId 消息id
     */
    @PatchMapping("/{noticeId}")
    @Log("推送消息")
    public Result<Void> pushNotice(@PathVariable Long noticeId) {
        noticeService.pushNotice(noticeId);
        return Result.ok();
    }

    /**
     * 删除通知
     *
     * @param noticeId 通知id
     */
    @DeleteMapping("/{noticeId}")
    @Log("删除消息")
    public Result<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return Result.ok();
    }

}
