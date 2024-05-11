package cn.daxpay.single.sdk.model.sync;

import cn.daxpay.single.sdk.code.PaySyncStatusEnum;
import cn.daxpay.single.sdk.net.DaxPayResponseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付订单同步结果
 * @author xxm
 * @since 2023/12/27
 */
@Getter
@Setter
@ToString
public class PaySyncModel extends DaxPayResponseModel {

    /**
     * 支付订单同步结果
     * @see PaySyncStatusEnum
     */
    private String status;

    /** 是否进行了修复 */
    private boolean repair;

    /** 修复号 */
    private String repairOrderNo;

}