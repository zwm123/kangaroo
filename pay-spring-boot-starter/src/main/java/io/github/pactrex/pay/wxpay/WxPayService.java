package io.github.pactrex.pay.wxpay;

import com.github.wxpay.sdk.WXPay;
import io.github.pactrex.pay.wxpay.autoconfigure.MyWxPayConfig;

public class WxPayService {

    private final WXPay wxPay;

    public WxPayService(MyWxPayConfig myWxPayConfig) throws Exception {
        this.wxPay = new WXPay(myWxPayConfig);
        this.wxPay.checkWXPayConfig();
    }

    public WXPay getWxPay() {
        return wxPay;
    }
}
