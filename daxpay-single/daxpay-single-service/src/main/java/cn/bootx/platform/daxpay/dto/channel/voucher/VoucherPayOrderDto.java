package cn.bootx.platform.daxpay.dto.channel.voucher;

import cn.bootx.platform.daxpay.dto.pay.order.BasePayOrderDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author xxm
 * @since 2022/3/14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "储值卡支付记录")
public class VoucherPayOrderDto extends BasePayOrderDto {

}