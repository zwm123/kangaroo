package io.github.pactrex.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import io.github.pactrex.pay.alipay.autoconfigure.AliPayConfig;
import io.github.pactrex.pay.alipay.request.AppPayRequest;
import io.github.pactrex.pay.alipay.request.PagePayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class AliPayService {

    private static final Logger logger = LoggerFactory.getLogger(AliPayService.class);
    private AlipayClient alipayClient;
    private AliPayConfig aliPayConfig;

    public AliPayService(AliPayConfig aliPayConfig) {
        this.aliPayConfig = aliPayConfig;
        this.alipayClient = new DefaultAlipayClient(
                aliPayConfig.getServerUrl(), aliPayConfig.getAppid(),
                aliPayConfig.getPrivateKey(), aliPayConfig.getFormat(), aliPayConfig.getCharset(),
                aliPayConfig.getAlipayPublicKey(), aliPayConfig.getSignType());
    }

    /**
     * 验证支付宝通知签名是否合法
     *
     * @param paramsMap
     * @return
     * @throws AlipayApiException
     */
    public boolean rsaCheckV1(Map<String, String> paramsMap) throws AlipayApiException {
        return AlipaySignature.rsaCheckV1(paramsMap, aliPayConfig.getAlipayPublicKey(), aliPayConfig.getCharset(), paramsMap.get("sign_type"));
    }

    /**
     * 获取支付宝App授权字符串
     *
     * @param targetId
     * @return
     * @throws AlipayApiException
     * @throws UnsupportedEncodingException
     */
    public String getInfoStr(String targetId) throws AlipayApiException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("apiname=com.alipay.account.auth")
                .append("&app_id=").append(aliPayConfig.getAppid())
                .append("&app_name=").append("mc")
                .append("&auth_type=AUTHACCOUNT")
                .append("&biz_type=openservice")
                .append("&method=alipay.open.auth.sdk.code.get")
                .append("&pid=").append(aliPayConfig.getSellerId())
                .append("&product_id=APP_FAST_LOGIN")
                .append("&scope=kuaijie")
                .append("&sign_type=").append(aliPayConfig.getSignType())
                .append("&target_id=").append(targetId);
        String sign = AlipaySignature.rsaSign(sb.toString(), aliPayConfig.getPrivateKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType());
        sb.append("&sign=").append(URLEncoder.encode(sign, aliPayConfig.getCharset()));
        return sb.toString();
    }

    /**
     * 获取支付宝App支付字符串
     *
     * @param request
     * @return
     * @throws AlipayApiException
     * @throws UnsupportedEncodingException
     */
    public String getOrderStr(AppPayRequest request) throws AlipayApiException, UnsupportedEncodingException {
        String originalStr = request.getOriginalStr();
        logger.debug("original string：{}", originalStr);
        String sign = AlipaySignature.rsaSign(originalStr, aliPayConfig.getPrivateKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType());
        logger.debug("signed string：{}", originalStr);
        request.setSign(sign);
        String encodedStr = request.getEncodedStr();
        logger.debug("encoded string：{}", originalStr);
        return encodedStr;
    }

    /**
     * 获取支付宝PC网页支付Form表单字符串
     *
     * @param pagePayRequest
     * @return
     * @throws AlipayApiException
     */
    public String getFormStr(PagePayRequest pagePayRequest) throws AlipayApiException {
        return alipayClient.pageExecute(pagePayRequest.generate()).getBody(); //调用SDK生成表单
    }

    public AlipayClient getAlipayClient() {
        return alipayClient;
    }

    public AliPayConfig getAliPayConfig() {
        return aliPayConfig;
    }
}
