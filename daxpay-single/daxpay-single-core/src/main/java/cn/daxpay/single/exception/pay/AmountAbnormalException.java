package cn.daxpay.single.exception.pay;

import cn.bootx.platform.common.core.exception.FatalException;
import cn.daxpay.single.code.DaxPayErrorCode;

/**
 * 异常金额
 *
 * @author xxm
 * @since 2020/12/8
 */
public class AmountAbnormalException extends FatalException {

    public AmountAbnormalException(String msg) {
        super(DaxPayErrorCode.PAYMENT_AMOUNT_ABNORMAL, msg);
    }

    public AmountAbnormalException() {
        super(DaxPayErrorCode.PAYMENT_AMOUNT_ABNORMAL, "异常金额");
    }

}