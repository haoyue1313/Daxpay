package cn.bootx.platform.daxpay.core.order.refund.convert;

import cn.bootx.platform.daxpay.core.order.refund.entity.PayRefundOrder;
import cn.bootx.platform.daxpay.dto.order.refund.PayRefundOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author xxm
 * @since 2022/3/2
 */
@Mapper
public interface RefundConvert {

    RefundConvert CONVERT = Mappers.getMapper(RefundConvert.class);

    PayRefundOrderDto convert(PayRefundOrder in);

}
