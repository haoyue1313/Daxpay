package cn.bootx.platform.daxpay.core.payment.repair.strategy;

import cn.bootx.platform.daxpay.code.PayChannelEnum;
import cn.bootx.platform.daxpay.func.AbsPayRepairStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 *
 * @author xxm
 * @since 2023/12/29
 */
@Slf4j
@Scope(SCOPE_PROTOTYPE)
@Service
@RequiredArgsConstructor
public class WalletPayRepairStrategy extends AbsPayRepairStrategy {
    /**
     * 策略标识
     *
     * @see PayChannelEnum
     */
    @Override
    public PayChannelEnum getType() {
        return null;
    }

    /**
     * 取消支付
     */
    @Override
    public void doCloseHandler() {

    }
}
