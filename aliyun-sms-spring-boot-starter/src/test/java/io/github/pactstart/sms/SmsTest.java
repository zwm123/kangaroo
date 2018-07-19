package io.github.pactstart.sms;

import com.aliyuncs.exceptions.ClientException;
import io.github.pactstart.sms.autoconfigure.SmsClient;
import io.github.pactstart.sms.autoconfigure.SmsConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SmsTest {

    private SmsClient smsClient;

    @Before
    public void init() {
        SmsConfig smsConfig = new SmsConfig();
        smsConfig.setAccessKeyId("xx");
        smsConfig.setAccessKeySecret("xx");
        this.smsClient = new SmsClient(smsConfig);
    }

    @Test
    public void testSms() throws ClientException {
        Map<String, String> params = new HashMap<>();
        params.put("code", "888888");
        smsClient.sendSms("signName", "SMS_139981597", "15000000000", params);
    }
}
