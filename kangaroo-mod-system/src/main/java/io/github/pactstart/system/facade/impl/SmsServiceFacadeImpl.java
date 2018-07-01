package io.github.pactstart.system.facade.impl;

import com.google.common.collect.Maps;
import io.github.pactstart.biz.common.errorcode.ResponseCode;
import io.github.pactstart.biz.common.exception.ApplicationException;
import io.github.pactstart.biz.common.utils.BeanValidator;
import io.github.pactstart.cache.autoconfigure.CacheService;
import io.github.pactstart.commonutils.DataUtils;
import io.github.pactstart.commonutils.JsonUtils;
import io.github.pactstart.commonutils.ValidUtils;
import io.github.pactstart.sms.autoconfigure.SmsClient;
import io.github.pactstart.sms.autoconfigure.SmsResponse;
import io.github.pactstart.system.entity.SmsTemplate;
import io.github.pactstart.system.facade.SmsServiceFacade;
import io.github.pactstart.system.facade.dto.*;
import io.github.pactstart.system.service.SmsRecordService;
import io.github.pactstart.system.service.SmsTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceFacadeImpl implements SmsServiceFacade {

    @Autowired
    private SmsClient smsClient;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private SmsRecordService smsRecordService;

    @Autowired
    private SmsTemplateService smsTemplateService;

    @Override
    public SmsSendResultDto sendNoticeSms(SmsSendParamDto smsSendParamDto) {
        BeanValidator.validate(smsSendParamDto);
        SmsTemplate smsTemplate = smsTemplateService.findByTemplateId(smsSendParamDto.getTemplateId());
        if (smsTemplate == null) {
            throw new ApplicationException(ResponseCode.INVALID_PARAM, "短信模板不存在");
        }
        if (!ValidUtils.isAllWildcardVariableValid(smsTemplate.getTemplate(), smsSendParamDto.getParams())) {
            log.error("短信模板存在变量未赋值", JsonUtils.obj2String(smsSendParamDto));
            throw new ApplicationException(ResponseCode.INVALID_PARAM, "短信模板存在变量未赋值");
        }
        SmsSendResultDto smsSendResultDto = new SmsSendResultDto();
        if (isRealSendSms()) {
            SmsResponse smsResponse = null;
            try {
                smsResponse = smsClient.sendSms(smsSendParamDto.getSignName(), smsSendParamDto.getTemplateId(), smsSendParamDto.getPhone(), smsSendParamDto.getParams());
                smsSendResultDto.setSuccess(smsResponse.isSuccess());
                smsSendResultDto.setFailReason(smsResponse.isSuccess() ? "发送成功" : smsResponse.getError());
            } catch (Exception e) {
                log.error("短信接口调用失败", e);
                smsSendResultDto.setSuccess(false);
                smsSendResultDto.setFailReason("短信接口调用失败");
                return smsSendResultDto;
            }
        } else {
            smsSendResultDto.setSuccess(true);
            smsSendResultDto.setFailReason("模拟发送成功");
        }
        smsRecordService.save(smsSendParamDto, smsSendResultDto, smsTemplate);
        return smsSendResultDto;
    }

    @Override
    public SmsSendResultDto sendValidateSms(SmsSendParamDto smsSendParamDto) {
        String code = DataUtils.numRandomGenerator(6);
        if (smsSendParamDto.getParams() == null) {
            smsSendParamDto.setParams(Maps.newHashMap());
        }
        smsSendParamDto.getParams().put("code", code);
        SmsSendResultDto smsSendResultDto = sendNoticeSms(smsSendParamDto);
        if (smsSendResultDto.isSuccess()) {
            String key = getKey(smsSendParamDto.getPhone(), smsSendParamDto.getTemplateId());
            cacheService.set(key, code);
            cacheService.expire(key, 5 * 60);
        }
        return smsSendResultDto;
    }

    @Override
    public SmsCodeValidateResultDto validateSmsCode(SmsCodeValidateParamDto smsCodeValidateParamDto) {
        BeanValidator.validate(smsCodeValidateParamDto);
        String key = getKey(smsCodeValidateParamDto.getPhone(), smsCodeValidateParamDto.getTemplateId());
        SmsCodeValidateResultDto resultDto = new SmsCodeValidateResultDto();
        if (!cacheService.exists(key)) {
            resultDto.setSuccess(false);
            resultDto.setFailReason("短信验证码已过期");
            return resultDto;
        }
        String code = cacheService.get(key);
        if (!smsCodeValidateParamDto.getCode().equals(code)) {
            resultDto.setSuccess(false);
            resultDto.setFailReason("短信验证码错误");
            return resultDto;
        }
        if (smsCodeValidateParamDto.isRemoveAfterPass()) {
            cacheService.delete(key);
        }
        resultDto.setSuccess(true);
        resultDto.setFailReason("短信验证码正确");
        return resultDto;
    }

    @Override
    public void removeSmsCode(SmsCodeRemoveDto smsCodeRemoveDto) {
        String key = getKey(smsCodeRemoveDto.getPhone(), smsCodeRemoveDto.getTemplateId());
        cacheService.delete(key);
    }

    private String getKey(String phone, String templateId) {
        return phone.concat("_").concat(templateId);
    }

    private boolean isRealSendSms() {
        return false;
    }
}
