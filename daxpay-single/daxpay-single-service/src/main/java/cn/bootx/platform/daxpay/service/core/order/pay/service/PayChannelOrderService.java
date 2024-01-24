package cn.bootx.platform.daxpay.service.core.order.pay.service;

import cn.bootx.platform.common.core.exception.DataNotExistException;
import cn.bootx.platform.common.core.util.ResultConvertUtil;
import cn.bootx.platform.daxpay.code.PayStatusEnum;
import cn.bootx.platform.daxpay.param.pay.PayChannelParam;
import cn.bootx.platform.daxpay.service.common.context.AsyncPayLocal;
import cn.bootx.platform.daxpay.service.common.local.PaymentContextLocal;
import cn.bootx.platform.daxpay.service.core.order.pay.dao.PayChannelOrderManager;
import cn.bootx.platform.daxpay.service.core.order.pay.entity.PayChannelOrder;
import cn.bootx.platform.daxpay.service.core.order.pay.entity.PayOrder;
import cn.bootx.platform.daxpay.service.dto.order.pay.PayChannelOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 支付订单关联通道服务
 * @author xxm
 * @since 2023/12/31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayChannelOrderService {
    private final PayChannelOrderManager channelOrderManager;

    /**
     * 根据支付ID查询列表
     */
    public List<PayChannelOrderDto> findAllByPaymentId(Long paymentId){
        return ResultConvertUtil.dtoListConvert(channelOrderManager.findAllByPaymentId(paymentId));
    }

    /**
     * 查询单条
     */
    public PayChannelOrderDto findById(Long id){
        return channelOrderManager.findById(id).map(PayChannelOrder::toDto).orElseThrow(() -> new DataNotExistException("通道支付订单未查到"));
    }

    /**
     * 更新支付订单的异步通道信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAsyncChannelOrder(PayOrder payOrder, PayChannelParam payChannelParam){
        AsyncPayLocal asyncPayInfo = PaymentContextLocal.get().getAsyncPayInfo();
        // 是否支付完成
        PayStatusEnum payStatus = asyncPayInfo.isPayComplete() ? PayStatusEnum.SUCCESS : PayStatusEnum.PROGRESS;
        Optional<PayChannelOrder> payOrderChannelOpt =
                channelOrderManager.findByPaymentIdAndChannel(payOrder.getId(), payChannelParam.getChannel());
        if (!payOrderChannelOpt.isPresent()){
            PayChannelOrder payChannelOrder = new PayChannelOrder();
            // 替换原有的的支付通道信息
            payChannelOrder.setPayWay(payChannelParam.getWay())
                    .setPaymentId(payOrder.getId())
                    .setAsync(true)
                    .setChannel(payChannelParam.getChannel())
                    .setPayWay(payChannelParam.getWay())
                    .setAmount(payChannelParam.getAmount())
                    .setRefundableBalance(payChannelParam.getAmount())
                    .setGatewayOrderNo(asyncPayInfo.getGatewayOrderNo())
                    .setPayTime(asyncPayInfo.getPayTime())
                    .setChannelExtra(payChannelParam.getChannelExtra())
                    .setStatus(payStatus.getCode());
            channelOrderManager.deleteByPaymentIdAndAsync(payChannelOrder.getId());
            channelOrderManager.save(payChannelOrder);
        } else {
            // 更新支付通道信息
            payOrderChannelOpt.get()
                    .setPayWay(payChannelParam.getWay())
                    .setGatewayOrderNo(asyncPayInfo.getGatewayOrderNo())
                    .setPayTime(asyncPayInfo.getPayTime())
                    .setChannelExtra(payChannelParam.getChannelExtra())
                    .setStatus(payStatus.getCode());
            channelOrderManager.updateById(payOrderChannelOpt.get());
        }
    }

}
