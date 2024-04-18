package cn.bootx.platform.daxpay.param.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分账同步请求参数
 * @author xxm
 * @since 2024/4/12
 */
@Data
@Schema(title = "分账同步请求参数")
public class AllocationSyncParam {

    @Schema(description = "分账单ID")
    private Long allocationId;

    @Schema(description = "分账单号")
    private String allocationNo;
}
