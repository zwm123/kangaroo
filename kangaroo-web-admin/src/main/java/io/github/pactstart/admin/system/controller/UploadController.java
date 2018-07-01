package io.github.pactstart.admin.system.controller;

import io.github.pactstart.admin.system.form.UploadPathForm;
import io.github.pactstart.biz.common.errorcode.ResponseCode;
import io.github.pactstart.biz.common.utils.MapperUtils;
import io.github.pactstart.commonutils.Base64Utils;
import io.github.pactstart.commonutils.JsonUtils;
import io.github.pactstart.oss.autoconfigure.PostPolicyResponse;
import io.github.pactstart.simple.web.framework.utils.ParamValidator;
import io.github.pactstart.system.dto.UploadPathDto;
import io.github.pactstart.system.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "资源上传")
@RequestMapping("/upload")
@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @ApiOperation(value = "获取上传policy")
    @ApiImplicitParam(name = "param", value = "上传场景", required = true, dataType = "UploadPathForm")
    @PostMapping(value = "/getOssSecurityToken.json")
    public ResponseCode getOssSecurityToken(@Valid @RequestBody UploadPathForm uploadPathForm, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        ParamValidator.validate(bindingResult);
        UploadPathDto uploadPathDto = MapperUtils.map(uploadPathForm, UploadPathDto.class);
        PostPolicyResponse postPolicy = uploadService.getPostPolicy(uploadPathDto);
        if (uploadPathForm.getCallback() != null && uploadPathForm.getCallback()) {
            Map<String, String> callbackData = new HashMap<>(6);
            //获取域名
            int endIndex = request.getRequestURL().length() - request.getRequestURI().length() + 1;
            String host = request.getRequestURL().substring(0, endIndex);

            callbackData.put("callbackUrl", host + "/oss/callback");
            callbackData.put("callbackHost", host);
            callbackData.put("callbackBody", "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
            callbackData.put("callbackBodyType", "application/x-www-form-urlencoded");
            postPolicy.setCallback(Base64Utils.encode(JsonUtils.obj2String(callbackData).getBytes()));
        }
        return ResponseCode.buildResponse(postPolicy);
    }

    @ApiOperation(value = "App获取上传凭证")
    @ApiImplicitParam(name = "param", value = "App获取上传凭证参数", required = true, dataType = "UploadPathForm")
    @PostMapping(value = "/getAppOssSecurityToken.json")
    public ResponseCode getAppOssSecurityToken(@RequestBody @Valid UploadPathForm uploadPathForm, BindingResult bindingResult) {
        ParamValidator.validate(bindingResult);
        UploadPathDto uploadPathDto = MapperUtils.map(uploadPathForm, UploadPathDto.class);
        return ResponseCode.buildResponse(uploadService.getOssSecurityToken(uploadPathDto));
    }
}
