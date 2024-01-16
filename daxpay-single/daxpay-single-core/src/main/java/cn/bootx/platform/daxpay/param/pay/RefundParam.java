package cn.bootx.platform.daxpay.param.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import java.util.List;

/**
 * 退款参数，适用于组合支付的订单退款操作中，
 * @author xxm
 * @since 2023/12/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(title = "退款参数")
public class RefundParam extends PayCommonParam {

    @Schema(description = "支付单ID")
    private Long paymentId;

    @Schema(description = "业务号")
    private String businessNo;

    /**
     * 部分退款需要传输支付通道参数参数
     */
    @Schema(description = "是否全部退款")
    private boolean refundAll;

    /**
     * 退款号, 可以为空, 但不可以重复, 部分退款时推荐传输
     */
    @Schema(description = "退款号")
    private String refundNo;

    /**
     * 部分退款时必传
     */
    @Valid
    @Schema(description = "退款参数列表")
    private List<RefundChannelParam> refundChannels;

    @Schema(description = "退款原因")
    private String reason;


}
