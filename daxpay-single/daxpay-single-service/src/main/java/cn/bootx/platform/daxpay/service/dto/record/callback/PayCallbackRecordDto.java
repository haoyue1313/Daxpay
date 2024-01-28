package cn.bootx.platform.daxpay.service.dto.record.callback;

import cn.bootx.platform.common.core.rest.dto.BaseDto;
import cn.bootx.platform.daxpay.code.PayChannelEnum;
import cn.bootx.platform.daxpay.service.code.PayCallbackStatusEnum;
import cn.bootx.platform.daxpay.service.code.PayCallbackTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 支付回调记录
 * @author xxm
 * @since 2024/1/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "回调记录")
public class PayCallbackRecordDto extends BaseDto {

    @Schema(description = "本地订单ID")
    private Long orderId;

    @Schema(description = "支付网关订单号")
    private String gatewayOrderNo;
    /**
     * 支付通道
     * @see PayChannelEnum#getCode()
     */
    @Schema(description = "支付通道")
    private String payChannel;

    /**
     * 回调类型
     * @see PayCallbackTypeEnum
     */
    @Schema(description = "回调类型")
    private String callbackType;

    /** 通知消息 */
    @Schema(description = "通知消息")
    private String notifyInfo;

    /**
     * 回调处理状态
     * @see PayCallbackStatusEnum
     */
    @Schema(description = "回调处理状态")
    private String status;

    @Schema(description = "支付单修复ID")
    private Long repairOrderId;

    /** 提示信息 */
    @Schema(description = "提示信息")
    private String msg;

}
