package io.github.pactstart.admin.system.controller;

import io.github.pactstart.admin.system.form.ConfigAddForm;
import io.github.pactstart.admin.system.form.ConfigLogQueryForm;
import io.github.pactstart.admin.system.form.ConfigQueryForm;
import io.github.pactstart.admin.system.form.ConfigUpdateForm;
import io.github.pactstart.biz.common.dto.PageResultDto;
import io.github.pactstart.biz.common.errorcode.ResponseCode;
import io.github.pactstart.biz.common.utils.MapperUtils;
import io.github.pactstart.simple.web.framework.auth.AuthenticationInfo;
import io.github.pactstart.simple.web.framework.utils.IpUtils;
import io.github.pactstart.simple.web.framework.utils.ParamValidator;
import io.github.pactstart.system.dto.*;
import io.github.pactstart.system.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "系统配置")
@RequestMapping(value = "/config", method = RequestMethod.POST, consumes = "application/json")
@RestController
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @ApiOperation(value = "分页查询配置")
    @ApiImplicitParam(name = "param", value = "查询条件", required = true, dataType = "ConfigQueryForm")
    @PostMapping("/query.json")
    public PageResultDto<ConfigDto> query(@RequestBody @Valid ConfigQueryForm param, BindingResult br) {
        ParamValidator.validate(br);
        ConfigQueryDto queryDto = MapperUtils.map(param, ConfigQueryDto.class);
        return configService.query(queryDto);
    }

    @ApiOperation(value = "添加配置")
    @ApiImplicitParam(name = "param", value = "添加参数", required = true, dataType = "ConfigAddForm")
    @PostMapping("/add.json")
    public ResponseCode add(@RequestBody @Valid ConfigAddForm param, BindingResult br, AuthenticationInfo authenticationInfo, HttpServletRequest request) {
        ParamValidator.validate(br);
        ConfigDto configDto = MapperUtils.map(param, ConfigDto.class);
        configDto.setOperator(authenticationInfo.getUserName());
        configDto.setOperateIp(IpUtils.getClientIpAddr(request));
        configService.add(configDto);
        return ResponseCode.SUCCESS;
    }

    @ApiOperation(value = "更新配置")
    @ApiImplicitParam(name = "param", value = "更新参数", required = true, dataType = "ConfigUpdateForm")
    @PostMapping("/update.json")
    public ResponseCode update(@RequestBody @Valid ConfigUpdateForm param, BindingResult br, AuthenticationInfo authenticationInfo, HttpServletRequest request) {
        ParamValidator.validate(br);
        ConfigUpdateDto configUpdateDto = MapperUtils.map(param, ConfigUpdateDto.class);
        configUpdateDto.setOperator(authenticationInfo.getUserName());
        configUpdateDto.setOperateIp(IpUtils.getClientIpAddr(request));
        configService.update(configUpdateDto);
        return ResponseCode.SUCCESS;
    }

    @ApiOperation(value = "分页查询配置日志")
    @ApiImplicitParam(name = "param", value = "查询条件", required = true, dataType = "ConfigLogQueryForm")
    @PostMapping("/queryLog.json")
    public PageResultDto<ConfigLogDto> query(@RequestBody @Valid ConfigLogQueryForm param, BindingResult br) {
        ParamValidator.validate(br);
        ConfigLogQueryDto queryDto = MapperUtils.map(param, ConfigLogQueryDto.class);
        return configService.queryLog(queryDto);
    }
}
