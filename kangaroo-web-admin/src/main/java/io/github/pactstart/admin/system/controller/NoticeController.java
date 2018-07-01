package io.github.pactstart.admin.system.controller;

import io.github.pactstart.admin.system.form.MemberNoticeQueryForm;
import io.github.pactstart.admin.system.form.PlatformNoticeAddForm;
import io.github.pactstart.admin.system.form.PlatformNoticeQueryForm;
import io.github.pactstart.admin.system.form.PlatformNoticeSendForm;
import io.github.pactstart.biz.common.dto.PageResultDto;
import io.github.pactstart.biz.common.errorcode.ResponseCode;
import io.github.pactstart.biz.common.utils.MapperUtils;
import io.github.pactstart.simple.web.framework.auth.AuthenticationInfo;
import io.github.pactstart.simple.web.framework.utils.IpUtils;
import io.github.pactstart.simple.web.framework.utils.ParamValidator;
import io.github.pactstart.system.dto.*;
import io.github.pactstart.system.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "通知")
@RequestMapping(value = "/notice", method = RequestMethod.POST, consumes = "application/json")
@RestController
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @ApiOperation(value = "添加平台通知")
    @ApiImplicitParam(name = "param", value = "添加条件", required = true, dataType = "PlatformNoticeAddForm")
    @PostMapping("/platform/add.json")
    public ResponseCode addPlatformNotice(@RequestBody @Valid PlatformNoticeAddForm platformNoticeAddForm, BindingResult br, AuthenticationInfo authenticationInfo, HttpServletRequest request) {
        ParamValidator.validate(br);
        PlatformNoticeAddDto platformNoticeAddDto = MapperUtils.map(platformNoticeAddForm, PlatformNoticeAddDto.class);
        platformNoticeAddDto.setOperator(authenticationInfo.getUserName());
        platformNoticeAddDto.setOperateIp(IpUtils.getClientIpAddr(request));
        noticeService.addPlatformNotice(platformNoticeAddDto);
        return ResponseCode.SUCCESS;
    }

    @ApiOperation(value = "发送平台通知")
    @ApiImplicitParam(name = "param", value = "发送条件", required = true, dataType = "PlatformNoticeSendForm")
    @PostMapping("/platform/send.json")
    public ResponseCode sendPlatformNotice(@RequestBody @Valid PlatformNoticeSendForm platformNoticeSendForm, BindingResult br) {
        ParamValidator.validate(br);
        PlatformNoticeSendDto sendDto = MapperUtils.map(platformNoticeSendForm, PlatformNoticeSendDto.class);
        noticeService.sendPlatformNotice(sendDto);
        return ResponseCode.SUCCESS;
    }

    @ApiOperation(value = "查询平台通知")
    @ApiImplicitParam(name = "param", value = "查询条件", required = true, dataType = "PlatformNoticeQueryForm")
    @PostMapping("/platform/query.json")
    public PageResultDto<PlatformNoticeDto> queryPlatformNotice(@Valid PlatformNoticeQueryForm queryForm, BindingResult br) {
        ParamValidator.validate(br);
        PlatformNoticeQueryDto queryDto = MapperUtils.map(queryForm, PlatformNoticeQueryDto.class);
        return noticeService.queryPlatformNotice(queryDto);
    }

    @ApiOperation(value = "查询会员通知")
    @ApiImplicitParam(name = "param", value = "查询条件", required = true, dataType = "MemberNoticeQueryForm")
    @PostMapping("/member/query.json")
    public PageResultDto<MemberNoticeDto> queryMemberNotice(@RequestBody @Valid MemberNoticeQueryForm queryForm, BindingResult br) {
        ParamValidator.validate(br);
        MemberNoticeQueryDto queryDto = MapperUtils.map(queryForm, MemberNoticeQueryDto.class);
        return noticeService.queryMemberNotice(queryDto);
    }
}
