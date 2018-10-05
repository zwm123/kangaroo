package io.github.pactstart.pay;

import io.github.pactstart.commonutils.JsonUtils;
import io.github.pactstart.pay.pipay.PiPayService;
import io.github.pactstart.pay.pipay.autoconfigure.PiPayConfig;
import io.github.pactstart.pay.pipay.request.PagePayRequest;
import io.github.pactstart.pay.pipay.response.OrderQueryResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class PiPayServiceTest {

    private PiPayService piPayService;

    @Before
    public void init() throws Exception {
        PiPayConfig piPayConfig = new PiPayConfig();
        piPayConfig.setMid("");
        piPayConfig.setSid("");
        piPayConfig.setDid("");
        piPayConfig.setCurrency("USD");
        piPayConfig.setTransactionUrl("https://onlinepayment-test.pipay.com/starttransaction");
        piPayConfig.setVerifyUrl("https://onlinepayment-test.pipay.com/rest-api/verifyTransaction");
        piPayService = new PiPayService(piPayConfig);
    }

    //    @Test
    public void testGetFormStr() {
        PagePayRequest pagePayRequest = PagePayRequest.builder().orderid(UUID.randomUUID().toString().replace("-", ""))
                .orderAmount("1.10").orderDesc("测试订单").build();
        String formStr = piPayService.getFormStr(pagePayRequest);
        System.out.println(formStr);
    }

    @Test
    public void testQueryOrder() {
        OrderQueryResponse orderQueryResponse = piPayService.orderQuery("211048246890308702208");
        System.out.println(JsonUtils.obj2String(orderQueryResponse));
    }

    //    @Test
    public void testQueryOrder2() {
        OrderQueryResponse orderQueryResponse = piPayService.orderQuery("02Oct2018", "573725");
        System.out.println(JsonUtils.obj2String(orderQueryResponse));
    }
}
