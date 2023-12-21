package cn.bootx.platform.daxpay.core.channel.cash.service;

import cn.bootx.platform.daxpay.code.PayStatusEnum;
import cn.bootx.platform.daxpay.core.channel.cash.dao.CashPaymentManager;
import cn.bootx.platform.daxpay.core.channel.cash.entity.CashPayOrder;
import cn.bootx.platform.daxpay.core.order.pay.entity.PayOrder;
import cn.bootx.platform.daxpay.param.pay.PayParam;
import cn.bootx.platform.daxpay.param.pay.PayWayParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 现金支付
 *
 * @author xxm
 * @since 2021/6/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CashService {

    private final CashPaymentManager cashPaymentManager;

    /**
     * 支付
     */
    public void pay(PayWayParam payMode, PayOrder payment, PayParam payParam) {
        CashPayOrder walletPayment = new CashPayOrder();
        walletPayment.setPaymentId(payment.getId())
            .setBusinessNo(payParam.getBusinessNo())
            .setAmount(payMode.getAmount())
            .setRefundableBalance(payMode.getAmount())
            .setStatus(payment.getStatus());
        cashPaymentManager.save(walletPayment);
    }

    /**
     * 关闭
     */
    public void close(Long paymentId) {
        Optional<CashPayOrder> cashPaymentOpt = cashPaymentManager.findByPaymentId(paymentId);
        cashPaymentOpt.ifPresent(cashPayOrder -> {
            cashPayOrder.setStatus(PayStatusEnum.CLOSE.getCode());
            cashPaymentManager.updateById(cashPayOrder);
        });
    }

    /**
     * 退款
     */
    public void refund(Long paymentId, int amount) {
        Optional<CashPayOrder> cashPayment = cashPaymentManager.findByPaymentId(paymentId);
        cashPayment.ifPresent(payOrder -> {
            int refundableBalance = payOrder.getRefundableBalance() - amount;
            // 全部退款
            if (refundableBalance == 0) {
                payOrder.setStatus(PayStatusEnum.REFUNDED.getCode());
            }
            // 部分退款
            else {
                payOrder.setStatus(PayStatusEnum.PARTIAL_REFUND.getName());
            }
            cashPaymentManager.updateById(payOrder);
        });
    }

}