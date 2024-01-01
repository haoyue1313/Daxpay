package cn.bootx.platform.daxpay.core.channel.alipay.service;

import cn.bootx.platform.daxpay.code.AliPayCode;
import cn.bootx.platform.daxpay.code.PayStatusEnum;
import cn.bootx.platform.daxpay.code.PaySyncStatusEnum;
import cn.bootx.platform.daxpay.common.local.PaymentContextLocal;
import cn.bootx.platform.daxpay.core.channel.alipay.dao.AliPayOrderManager;
import cn.bootx.platform.daxpay.core.record.pay.entity.PayOrder;
import cn.bootx.platform.daxpay.core.payment.sync.result.GatewaySyncResult;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ijpay.alipay.AliPayApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 支付宝同步
 *
 * @author xxm
 * @since 2021/5/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliPaySyncService {
    private final AliPayOrderManager payOrderManager;

    /**
     * 与支付宝网关同步状态, 退款状态会参加
     * 1 远程支付成功
     * 2 交易创建，等待买家付款
     * 3 超时关闭
     * 4 查询不到
     * 5 查询失败
     */
    public GatewaySyncResult syncPayStatus(PayOrder payOrder) {
        GatewaySyncResult syncResult = new GatewaySyncResult().setSyncStatus(PaySyncStatusEnum.FAIL.getCode());
        // 查询
        try {
            AlipayTradeQueryModel queryModel = new AlipayTradeQueryModel();
            queryModel.setOutTradeNo(String.valueOf(payOrder.getId()));
            // 查询退款参数
            AlipayTradeQueryResponse response = AliPayApi.tradeQueryToResponse(queryModel);
            String tradeStatus = response.getTradeStatus();
            syncResult.setJson(JSONUtil.toJsonStr(response));
            // 支付完成
            if (Objects.equals(tradeStatus, AliPayCode.PAYMENT_TRADE_SUCCESS) || Objects.equals(tradeStatus, AliPayCode.PAYMENT_TRADE_FINISHED)) {
                PaymentContextLocal.get().getAsyncPayInfo().setTradeNo(response.getTradeNo());
                return syncResult.setSyncStatus(PaySyncStatusEnum.PAY_SUCCESS.getCode());
            }
            // 待支付
            if (Objects.equals(tradeStatus, AliPayCode.PAYMENT_WAIT_BUYER_PAY)) {
                return syncResult.setSyncStatus(PaySyncStatusEnum.PAY_WAIT.getCode());
            }
            // 已关闭或支付完成后全额退款
            if (Objects.equals(tradeStatus, AliPayCode.PAYMENT_TRADE_CLOSED)) {
                // 根据支付订单区分退款的情况, TODO 后期添加查询退款信息的逻辑
                if (Objects.equals(payOrder.getStatus(), PayStatusEnum.REFUNDED.getCode())){
                    return syncResult.setSyncStatus(PaySyncStatusEnum.REFUND.getCode());
                } else {
                    return syncResult.setSyncStatus(PaySyncStatusEnum.CLOSED.getCode());
                }
            }
            // 未找到
            if (Objects.equals(response.getSubCode(), AliPayCode.ACQ_TRADE_NOT_EXIST)) {
                return syncResult.setSyncStatus(PaySyncStatusEnum.NOT_FOUND.getCode());
            }
        }
        catch (AlipayApiException e) {
            log.error("查询订单失败:", e);
            syncResult.setMsg(e.getErrMsg());
        }
        return syncResult;
    }
}