package cn.bootx.platform.daxpay.core.channel.alipay.service;

import cn.bootx.platform.common.spring.exception.RetryableException;
import cn.bootx.platform.daxpay.code.AliPayCode;
import cn.bootx.platform.daxpay.core.order.pay.entity.PayOrder;
import cn.bootx.platform.daxpay.core.payment.refund.local.AsyncRefundLocal;
import cn.bootx.platform.daxpay.exception.pay.PayFailureException;
import cn.hutool.core.util.IdUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeCancelModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ijpay.alipay.AliPayApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 支付宝支付取消和支付关闭
 *
 * @author xxm
 * @since 2021/4/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliPayCloseService {

    /**
     * 关闭支付
     */
    @Retryable(value = RetryableException.class)
    public void cancelRemote(PayOrder payOrder) {
        // 只有部分需要调用支付宝网关进行关闭
        AlipayTradeCancelModel model = new AlipayTradeCancelModel();
        model.setOutTradeNo(String.valueOf(payOrder.getId()));

        try {
            AlipayTradeCancelResponse response = AliPayApi.tradeCancelToResponse(model);
            if (!Objects.equals(AliPayCode.SUCCESS, response.getCode())) {
                log.error("网关返回撤销失败: {}", response.getSubMsg());
                throw new PayFailureException(response.getSubMsg());
            }
        }
        catch (AlipayApiException e) {
            log.error("关闭订单失败:", e);
            throw new PayFailureException("关闭订单失败");
        }
    }

    /**
     * 退款
     */
    public void refund(PayOrder payOrder, BigDecimal amount) {
        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setOutTradeNo(String.valueOf(payOrder.getId()));
        refundModel.setRefundAmount(amount.toPlainString());

        // 设置退款号
        AsyncRefundLocal.set(IdUtil.getSnowflakeNextIdStr());
        refundModel.setOutRequestNo(AsyncRefundLocal.get());
        try {
            AlipayTradeRefundResponse response = AliPayApi.tradeRefundToResponse(refundModel);
            if (!Objects.equals(AliPayCode.SUCCESS, response.getCode())) {
                AsyncRefundLocal.setErrorMsg(response.getSubMsg());
                AsyncRefundLocal.setErrorCode(response.getCode());
                log.error("网关返回退款失败: {}", response.getSubMsg());
                throw new PayFailureException(response.getSubMsg());
            }
        }
        catch (AlipayApiException e) {
            log.error("订单退款失败:", e);
            AsyncRefundLocal.setErrorMsg(e.getErrMsg());
            AsyncRefundLocal.setErrorCode(e.getErrCode());
            throw new PayFailureException("订单退款失败");
        }
    }

}