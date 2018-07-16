package io.github.pactrex.pay.wxpay.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderQueryRequest {

    /**
     * 微信的订单号，优先使用
     */
    private String transaction_id;

    /**
     * 商户系统内部的订单号，当没提供transaction_id时需要传这个。
     */
    private String out_trade_no;

}
