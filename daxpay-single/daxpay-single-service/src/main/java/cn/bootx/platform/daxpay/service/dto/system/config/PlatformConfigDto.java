package cn.bootx.platform.daxpay.service.dto.system.config;

import cn.bootx.platform.daxpay.code.PaySignTypeEnum;
import cn.bootx.table.modify.annotation.DbColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 平台配置
 * @author xxm
 * @since 2024/1/2
 */
@Data
@Accessors(chain = true)
@Schema(title = "平台配置")
public class PlatformConfigDto {

    @DbColumn(comment = "网站地址")
    private String websiteUrl;

    /**
     * @see PaySignTypeEnum
     */
    @DbColumn(comment = "签名方式")
    private String signType;

    @DbColumn(comment = "签名秘钥")
    private String signSecret;

    @DbColumn(comment = "支付通知地址")
    private String notifyUrl;

    @DbColumn(comment = "订单默认超时时间")
    private Integer orderTimeout;
}