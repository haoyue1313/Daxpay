package cn.bootx.platform.daxpay.service.dto.allocation;

import cn.bootx.platform.common.core.rest.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 * @author xxm
 * @since 2024/4/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "")
public class AllocationGroupDto extends BaseDto {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "通道")
    private String channel;

    @Schema(description = "总分账比例(万分之多少)")
    private Integer totalRate;

    @Schema(description = "备注")
    private String remark;
}