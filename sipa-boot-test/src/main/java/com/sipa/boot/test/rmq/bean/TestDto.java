package com.sipa.boot.test.rmq.bean;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wkmtem Queue
 */
@Data
public class TestDto {
    @Schema(required = true, description = "消息追踪ID")
    @NotNull(message = "消息追踪ID不能为空")
    private Long trackId = -1L;

    @Schema(required = true, description = "交易类型[1: 充值+, 2: 提现-, 3: 退款+, 4: 消费-]")
    @NotNull(message = "交易类型不能为空")
    // @Range(min = 1, max = 4, message = "交易类型范围[{min}-{max}]")
    private TestEnum tradeType;

    @Schema(description = "银行订单ID")
    private Long bankOrderId;

    @Schema(description = "交易失败信息")
    private String failedMsg = "";

    @Schema(description = "交易完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "银行二清商户ID[企业福利账户/会员平台账户/会员福利账户/商家平台账户]")
    private Long merchantId;

    @Schema(description = "发生金额[分]")
    private Integer occurAmount;
}
