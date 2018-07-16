package io.github.pactrex.pay.wxpay.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coupon {

    /**
     * 代金券或立减优惠ID,
     * String(20)
     */
    private String coupon_id;

    /**
     * CASH--充值代金券
     * NO_CASH---非充值优惠券
     * String
     */
    private String coupon_type;

    /**
     * 单个代金券或立减优惠支付金额
     */
    private Integer coupon_fee;
}
