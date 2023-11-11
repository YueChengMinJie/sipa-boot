package com.sipa.boot.test.rmq.bean;

import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wkm
 * @date 2023/8/22
 */
@AllArgsConstructor
@Schema(enumAsRef = true, type = "integer", example = "1",
    description = "1: 充值+, 2: 提现-, 3: 退款+, 4: 消费-, 5: 正调+, 6: 负调-, 7:转入+, 8: 转出-")
public enum TestEnum implements IEnum<Integer> {
    TRADE_TYPE_OF_RECHARGE(1, "充值"),
    TRADE_TYPE_OF_CASH_OUT(2, "提现"),
    TRADE_TYPE_OF_REFUND(3, "退款"),
    TRADE_TYPE_OF_CONSUME(4, "消费"),
    TRADE_TYPE_OF_PLUS(5, "正调"),
    TRADE_TYPE_OF_MINUS(6, "负调"),
    TRADE_TYPE_OF_TRANSFER_IN(7, "转入"),
    TRADE_TYPE_OF_TRANSFER_OUT(8, "转出");

    @Getter(onMethod_ = @JsonValue)
    private final int code;

    @Getter
    private final String name;

    public static TestEnum get(Integer index) {
        return index == null ? null
            : Arrays.stream(values()).filter(x -> index.equals(x.code)).findFirst().orElse(null);
    }

    @Override
    public Integer getValue() {
        return getCode();
    }
}
