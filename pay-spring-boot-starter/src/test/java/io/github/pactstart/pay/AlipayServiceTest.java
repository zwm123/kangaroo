package io.github.pactstart.pay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.*;
import io.github.pactstart.pay.alipay.AliPayService;
import io.github.pactstart.pay.alipay.autoconfigure.AliPayConfig;
import io.github.pactstart.pay.alipay.request.AppPayRequest;
import io.github.pactstart.pay.alipay.request.PagePayRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
public class AlipayServiceTest {

    private AliPayService aliPayService;

    @Before
    public void init() throws Exception {
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setAppid("");
        aliPayConfig.setSellerId("");
        aliPayConfig.setAppName("");
        aliPayConfig.setAlipayPublicKey("");
        aliPayConfig.setPrivateKey("");
        aliPayService = new AliPayService(aliPayConfig);
    }

    @Test
    public void testPagePay() throws AlipayApiException {
        PagePayRequest pagePayRequest = new PagePayRequest.PagePayRequestBuilder().setAppId("2017071007701156")
                .setTimeoutExpress("30m")
                .setTotalAmount("70.00")
                .setSubject("我有单-购买货品费用:穿越火线|代练服务|10块钱升到20")
                .setOutTradeNo("0912598189608144896")
                .setNotifyUrl("http://localhost/pay/notify/alipay-app")
                .setReturnUrl("http://localhost/pay/return/alipay-app")
                .build();
        String formStr = aliPayService.getFormStr(pagePayRequest);
        log.info(formStr);
    }

    @Test
    public void testAppPay() throws AlipayApiException, UnsupportedEncodingException {
        AliPayConfig aliPayConfig = aliPayService.getAliPayConfig();
        AppPayRequest appPayRequest = new AppPayRequest.AppPayRequestBuilder().setAppId(aliPayConfig.getAppid())
                .setTimeoutExpress("30m")
                .setTotalAmount("70.00")
                .setSubject("我有单-购买货品费用:穿越火线|代练服务|10块钱升到20")
                .setOutTradeNo("0912598189608144896")
                .setNotifyUrl("http://localhost/pay/notify/alipay-app").build();
        String originalStr = appPayRequest.getOriginalStr();
        log.info("originalStr ： {}", originalStr);
        String sign = AlipaySignature.rsaSign(originalStr, aliPayConfig.getPrivateKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType());
        log.info("sign ： {}", sign);
        appPayRequest.setSign(sign);
        String signedStr = originalStr + "&sign=" + sign;
        log.info("signedStr ： {}", signedStr);
        String encodedStr = appPayRequest.getEncodedStr();
        log.info("encodedStr ： {}", encodedStr);
    }

    @Test
    public void testTradeQuery() throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel queryModel = new AlipayTradeQueryModel();
        queryModel.setTradeNo("2017092621001004290514021797");
        queryModel.setOutTradeNo("30946284938326577152");
        request.setBizModel(queryModel);
        AlipayTradeQueryResponse response = aliPayService.query(request);
        log.info(JSON.toJSONString(response));
    }

    @Test
    public void testRefund() {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setOutTradeNo("30912598189608144896");
        refundModel.setTradeNo("2017092621001004290514021797");
        refundModel.setOperatorId("Rex");
        refundModel.setRefundAmount("0.1");
        refundModel.setRefundReason("测试退款");
        request.setBizModel(refundModel);
        try {
            AlipayTradeRefundResponse response = aliPayService.refund(request);
            log.info(JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTransfer() {
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        AlipayFundTransToaccountTransferModel transferModel = new AlipayFundTransToaccountTransferModel();
        transferModel.setAmount("0.1");
        transferModel.setPayeeType("ALIPAY_USERID");
        transferModel.setPayeeAccount("2088312853977924");
        transferModel.setPayerShowName("爱淘游");
        transferModel.setPayeeRealName("刘念君");
        transferModel.setRemark("转账备注");
        transferModel.setOutBizNo(UUID.randomUUID().toString().replace("-", ""));
        request.setBizModel(transferModel);
        try {
            AlipayFundTransToaccountTransferResponse response = aliPayService.transfer(request);
            log.info(JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetInfoStr() {
        try {
            String infoStr = aliPayService.getInfoStr("Rex");
            log.info(infoStr);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAuthToken() {
        try {
            AlipaySystemOauthTokenResponse response = aliPayService.getAuthToken("df2fd9f7d7364319ae5a71be0a1bUB92");
            log.info(JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUserInfo() {
        try {
            AlipayUserInfoShareResponse response = aliPayService.getUserInfo("kuaijieB922c5dcec59e49009fa0f12df6505X92");
            log.info(JSON.toJSONString(response));
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

}
