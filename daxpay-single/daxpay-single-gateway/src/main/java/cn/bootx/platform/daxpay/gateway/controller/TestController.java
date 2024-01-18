package cn.bootx.platform.daxpay.gateway.controller;

import cn.bootx.platform.common.core.exception.BizException;
import cn.bootx.platform.common.core.rest.Res;
import cn.bootx.platform.common.core.rest.ResResult;
import cn.bootx.platform.daxpay.service.core.channel.alipay.service.AlipayReconcileService;
import cn.bootx.platform.daxpay.service.core.channel.wechat.service.WechatPayReconcileService;
import cn.bootx.platform.daxpay.service.core.timeout.task.PayExpiredTimeTask;
import cn.bootx.platform.daxpay.service.core.timeout.task.PayWaitOrderSyncTask;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 测试
 * @author xxm
 * @since 2024/1/5
 */
@Tag(name = "测试")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final PayExpiredTimeTask expiredTimeTask;;
    private final PayWaitOrderSyncTask waitOrderSyncTask;
    private final AlipayReconcileService alipayReconcileService;
    private final WechatPayReconcileService wechatPayReconcileService;
    private final LockTemplate lockTemplate;

    @Operation(summary = "同步")
    @GetMapping("/sync")
    public ResResult<Void> sync(){
        waitOrderSyncTask.task();
        return Res.ok();
    }
    @Operation(summary = "超时")
    @GetMapping("/expired")
    public ResResult<Void> expired(){
        expiredTimeTask.task();
        return Res.ok();
    }

    @Operation(summary = "锁测试1")
    @GetMapping("/lock1")
//    @Lock4j(keys = "#name", acquireTimeout = 50)
    public ResResult<String> lock1(String name){
        LockInfo lock = lockTemplate.lock(name, 10000, 10);
        if (Objects.isNull(lock)){
            throw new BizException("未获取到锁");
        }
        System.out.println("进来了......");
        ThreadUtil.sleep(10000);
        lockTemplate.releaseLock(lock);
        return Res.ok(name);
    }


    @Operation(summary = "锁测试2")
    @GetMapping("/lock2")
//    @Lock4j(keys = "#name", acquireTimeout = 50)
    public ResResult<String> lock2(String name){
        return Res.ok(name);
    }

    @Operation(summary = "下载支付宝对账单")
    @GetMapping("/aliDownReconcile")
    public ResResult<String> aliDownReconcile(String date){
        alipayReconcileService.downAndSave(date);
        return Res.ok();
    }
    @Operation(summary = "下载微信对账单")
    @GetMapping("/wxDownReconcile")
    public ResResult<Void> wxDownReconcile(String date){
        wechatPayReconcileService.downAndSave(date,null);
        return Res.ok();
    }

}
