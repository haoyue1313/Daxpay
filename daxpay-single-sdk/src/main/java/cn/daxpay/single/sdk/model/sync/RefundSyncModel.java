package cn.daxpay.single.sdk.model.sync;

import cn.daxpay.single.sdk.code.RefundSyncStatusEnum;
import cn.daxpay.single.sdk.net.DaxPayResponseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 退款信息同步结果
 * @author xxm
 * @since 2024/2/7
 */
@Getter
@Setter
@ToString
public class RefundSyncModel extends DaxPayResponseModel {

    /**
     * 支付网关同步结果
     * @see RefundSyncStatusEnum
     *
     */
    private String status;

    /** 是否进行了修复 */
    private boolean repair;

    /** 修复号 */
    private String repairOrderNo;

}