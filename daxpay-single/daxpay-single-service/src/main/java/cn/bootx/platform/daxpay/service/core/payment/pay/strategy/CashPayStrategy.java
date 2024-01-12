package cn.bootx.platform.daxpay.service.core.payment.pay.strategy;

import cn.bootx.platform.daxpay.code.PayChannelEnum;
import cn.bootx.platform.daxpay.service.core.channel.cash.service.CashService;
import cn.bootx.platform.daxpay.service.core.order.pay.service.PayOrderService;
import cn.bootx.platform.daxpay.exception.pay.PayAmountAbnormalException;
import cn.bootx.platform.daxpay.service.func.AbsPayStrategy;
import cn.bootx.platform.daxpay.param.pay.PayChannelParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 现金支付
 *
 * @author xxm
 * @since 2021/6/23
 */
@Slf4j
@Scope(SCOPE_PROTOTYPE)
@Service
@RequiredArgsConstructor
public class CashPayStrategy extends AbsPayStrategy {

    private final CashService cashService;

    private final PayOrderService payOrderService;

    /**
     * 现金支付
     */
    @Override
    public PayChannelEnum getType() {
        return PayChannelEnum.CASH;
    }

    /**
     * 支付前检查
     */
    @Override
    public void doBeforePayHandler() {
        // 检查金额
        PayChannelParam payChannelParam = this.getPayChannelParam();
        if (payChannelParam.getAmount() <= 0) {
            throw new PayAmountAbnormalException();
        }
    }

    /**
     * 支付操作
     */
    @Override
    public void doPayHandler() {
        cashService.pay(this.getPayChannelParam(), this.getOrder(), this.getPayParam());
    }

    /**
     * 关闭本地支付记录
     */
    @Override
    public void doCloseHandler() {
        cashService.close(this.getOrder().getId());
    }

}