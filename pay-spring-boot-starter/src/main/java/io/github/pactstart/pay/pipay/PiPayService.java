package io.github.pactstart.pay.pipay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.pactstart.commonutils.DataUtils;
import io.github.pactstart.commonutils.DateUtils;
import io.github.pactstart.http.wrapper.HttpUtils;
import io.github.pactstart.pay.pipay.autoconfigure.PiPayConfig;
import io.github.pactstart.pay.pipay.request.PagePayRequest;
import io.github.pactstart.pay.pipay.response.OrderQueryResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PiPayService {

    private PiPayConfig piPayConfig;

    public PiPayService(PiPayConfig piPayConfig) {
        this.piPayConfig = piPayConfig;
    }

    public String getFormStr(PagePayRequest request) {
        String digest = DataUtils.md5(piPayConfig.getMid().concat(request.getOrderid()).concat(request.getOrderAmount())).toLowerCase();
        String formStr = "<form id=\"pipayform\" name=\"pipayform\" action=\"https://onlinepayment-test.pipay.com/starttransaction\" method=\"post\">\n" +
                "            <input type=\"hidden\" name=\"mid\" value=\"" + piPayConfig.getMid() + "\"/>\n" +
                "            <input type=\"hidden\" name=\"lang\" value=\"en\"/>\n" +
                "            <input type=\"hidden\" name=\"orderid\" value=\"" + request.getOrderid() + "\"/>\n" +
                "            <input type=\"hidden\" name=\"orderDesc\" value=\"" + request.getOrderDesc() + "\"/>\n" +
                "            <input type=\"hidden\" name=\"orderAmount\" value=\"" + request.getOrderAmount() + "\"/>\n" +
                "            <input type=\"hidden\" name=\"currency\" value=\"USD\"/>\n" +
                "            <input type=\"hidden\" name=\"sid\" value=\"" + piPayConfig.getSid() + "\"/>\n" +
                "            <input type=\"hidden\" name=\"did\" value=\"" + piPayConfig.getDid() + "\"/>\n" +
                "            <input type=\"hidden\" name=\"orderDate\" value=\"" + DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss") + "\"/>\n" +
                "            <input type=\"hidden\" name=\"payMethod\" value=\"wallet\"/>\n" +
                "            <input type=\"hidden\" name=\"trType\" value=\"2\"/>\n" +
                "            <input type=\"hidden\" name=\"cancelTimer\" value=\"0\" />\n" +
                "            <input type=\"hidden\" name=\"confirmURL\" value=\"" + piPayConfig.getConfirmUrl() + "\"/>\n" +
                "            <input type=\"hidden\" name=\"cancelURL\" value=\"" + piPayConfig.getCancelUrl() + "\"/>\n";
        if (request.getExtParams() != null && request.getExtParams().size() > 0) {
            for (Map.Entry<String, String> entry : request.getExtParams().entrySet()) {
                formStr = formStr + "            <input type=\"hidden\" name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\"/>\n";
            }
        }
        formStr = formStr + "            <input type=\"hidden\" name=\"digest\" value=\"" + digest + "\"/>\n" +
                "            <input type=\"image\" name=\"submit\"  src=\"https://www.paypalobjects.com/en_US/i/btn/btn_buynow_LG.gif\"  alt=\"PiPay - The safer, easier way to pay online\" />\n" +
                "            <input type=\"hidden\" name=\"cancelTimer\" value=\"" + request.getCancelSeconds() + "\" />" +
                "        </form>" +
                " <script>\n" +
                "\tdocument.pipayform.submit();\n" +
                " </script>";
        return formStr;
    }

    public OrderQueryResponse orderQuery(String orderId, String processId) {
        Map<String, String> params = new HashMap<>(4);
        params.put("orderID", orderId);
        params.put("processID", processId);
        Map<String, Object> data = new HashMap<>(2);
        data.put("data", params);
        String json = HttpUtils.post(piPayConfig.getVerifyUrl()).setParameterJson(data).addHeader("Cache-Control", "no-cache").execute().getString();
        log.info(json);
        OrderQueryResponse orderQueryResponse = parseQueryResult(json);
        return orderQueryResponse;
    }

    private OrderQueryResponse parseQueryResult(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String resultCode = jsonObject.getString("resultCode");
        OrderQueryResponse orderQueryResponse = new OrderQueryResponse();
        orderQueryResponse.setResultCode(resultCode);
        if ("0000".equals(resultCode)) {
            String processorResponseCode = jsonObject.getString("processorResponseCode");
            String amount = jsonObject.getString("amount");
            String currency = jsonObject.getString("currency");
            String processorID = jsonObject.getString("processorID");

            orderQueryResponse.setAmount(amount);
            orderQueryResponse.setProcessorResponseCode(processorResponseCode);
            orderQueryResponse.setCurrency(currency);
            orderQueryResponse.setProcessorID(processorID);
        }
        return orderQueryResponse;
    }

    public OrderQueryResponse orderQuery(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("orderID", orderId);
        params.put("mid", piPayConfig.getMid());
        params.put("sid", piPayConfig.getSid());
        Map<String, Object> data = new HashMap<>(2);
        data.put("data", params);
        String json = HttpUtils.post(piPayConfig.getVerifyUrl()).setParameterJson(data).execute().getString();
        log.info(json);
        OrderQueryResponse orderQueryResponse = parseQueryResult(json);
        return orderQueryResponse;
    }
}
