package io.github.pactrex.pay.wxpay;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.pactrex.pay.wxpay.autoconfigure.MyWxPayConfig;
import io.github.pactrex.pay.wxpay.autoconfigure.WxPayProperties;
import io.github.pactrex.pay.wxpay.exception.WxPayException;
import io.github.pactrex.pay.wxpay.model.Coupon;
import io.github.pactrex.pay.wxpay.request.AppUnifiedOrderRequest;
import io.github.pactrex.pay.wxpay.request.OrderQueryRequest;
import io.github.pactrex.pay.wxpay.response.AppUnifiedOrderResponse;
import io.github.pactrex.pay.wxpay.response.OrderQueryResponse;
import io.github.pactstart.biz.common.utils.BeanMapUtils;
import io.github.pactstart.biz.common.utils.BeanValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class WxPayService {

    private final WXPay wxPay;

    private final WxPayProperties wxPayProperties;

    public WxPayService(WxPayProperties wxPayProperties) throws Exception {
        this.wxPayProperties = wxPayProperties;
        this.wxPay = new WXPay(new MyWxPayConfig(wxPayProperties), wxPayProperties.getNotifyUrl(), wxPayProperties.isAutoReport(), wxPayProperties.isUseSandbox());
        this.wxPay.checkWXPayConfig();
        if (StringUtils.isBlank(wxPayProperties.getNotifyUrl())) {
            throw new Exception("notifyUrl in config is empty");
        }
    }

    public WXPay getWxPay() {
        return wxPay;
    }

    public WxPayProperties getWxPayProperties() {
        return wxPayProperties;
    }

    private Map<String, String> getParamMap(Object request) {
        Map<String, Object> paramMap = BeanMapUtils.beanToMap(request);
        Map<String, String> filteredParamMap = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (entry.getValue() != null) {
                filteredParamMap.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return filteredParamMap;
    }

    private void validateParam(Object request) {
        Map<String, String> invalidParamMap = BeanValidator.validate(request);
        if (invalidParamMap != null && invalidParamMap.size() > 0) {
            log.error("调用微信支付相关接口参数不合法", JSON.toJSONString(invalidParamMap));
            throw new WxPayException("参数不合法");
        }
    }

    public AppUnifiedOrderResponse appUnifiedOrder(AppUnifiedOrderRequest request) throws Exception {
        validateParam(request);
        Map<String, String> paramMap = getParamMap(request);
        Map<String, String> responseParamMap = wxPay.unifiedOrder(paramMap);
        AppUnifiedOrderResponse response = JSON.parseObject(JSON.toJSONString(responseParamMap), AppUnifiedOrderResponse.class);
        return response;
    }

    public Map<String, String> getAppPayParam(AppUnifiedOrderResponse response) throws Exception {
        Map<String, String> appPayParamMap = Maps.newHashMap();
        appPayParamMap.put("appid", response.getAppid());
        appPayParamMap.put("partnerid", response.getMch_id());
        appPayParamMap.put("prepayid", response.getPrepay_id());
        appPayParamMap.put("package", "Sign=WXPay");
        appPayParamMap.put("noncestr", WXPayUtil.generateNonceStr());
        appPayParamMap.put("timestamp", WXPayUtil.getCurrentTimestamp() + "");
        appPayParamMap.put("sign", WXPayUtil.generateSignature(appPayParamMap, wxPayProperties.getKey()));
        return appPayParamMap;
    }

    public OrderQueryResponse orderQuery(OrderQueryRequest request) throws Exception {
        validateParam(request);
        Map<String, String> paramMap = getParamMap(request);
        Map<String, String> responseParamMap = wxPay.orderQuery(paramMap);
        OrderQueryResponse response = JSON.parseObject(JSON.toJSONString(responseParamMap), OrderQueryResponse.class);
        if (response.isSuccess()) {
            if (responseParamMap.get("coupon_count") != null) {
                int coupon_count = Integer.valueOf(responseParamMap.get("coupon_count"));
                List<Coupon> couponList = Lists.newArrayList();
                for (int i = 0; i < coupon_count; i++) {
                    Coupon coupon = new Coupon();
                    coupon.setCoupon_id(responseParamMap.get("coupon_id_" + i));
                    coupon.setCoupon_type(responseParamMap.get("coupon_type_" + i));
                    coupon.setCoupon_fee(Integer.valueOf("coupon_fee_" + i));
                    couponList.add(coupon);
                }
                response.setCouponList(couponList);
            }
        }
        return response;
    }
}
