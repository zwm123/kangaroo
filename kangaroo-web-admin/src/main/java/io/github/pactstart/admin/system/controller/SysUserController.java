package io.github.pactstart.admin.system.controller;

import com.google.common.collect.Maps;
import io.github.pactstart.admin.auth.AdminAuthorizationInfo;
import io.github.pactstart.admin.auth.AdminConstants;
import io.github.pactstart.admin.system.form.LoginForm;
import io.github.pactstart.admin.system.form.UserForm;
import io.github.pactstart.admin.system.form.UserIdForm;
import io.github.pactstart.admin.system.form.UserQueryForm;
import io.github.pactstart.biz.common.dto.PageResultDto;
import io.github.pactstart.biz.common.errorcode.ResponseCode;
import io.github.pactstart.biz.common.utils.MapperUtils;
import io.github.pactstart.commonutils.DataUtils;
import io.github.pactstart.simple.web.framework.auth.AuthenticationInfo;
import io.github.pactstart.simple.web.framework.auth.SimpleUserInfo;
import io.github.pactstart.simple.web.framework.utils.IpUtils;
import io.github.pactstart.simple.web.framework.utils.ParamValidator;
import io.github.pactstart.system.dto.SysUserDto;
import io.github.pactstart.system.dto.UserQueryDto;
import io.github.pactstart.system.entity.SysUser;
import io.github.pactstart.system.enums.SysUserStatus;
import io.github.pactstart.system.errorcode.SysResponseCode;
import io.github.pactstart.system.service.SysRoleService;
import io.github.pactstart.system.service.SysTreeService;
import io.github.pactstart.system.service.SysUserService;
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
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@Api(tags = "系统用户")
@RequestMapping("/sys/user")
@RestController
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysTreeService sysTreeService;

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "用户名或邮箱密码登录")
    @ApiImplicitParam(name = "param", value = "用户参数", required = true, dataType = "LoginForm")
    @PostMapping(value = "/login.json")
    public ResponseCode login(@RequestBody @Valid LoginForm loginForm, BindingResult bindingResult, HttpSession session) {
        ParamValidator.validate(bindingResult);
        SysUser sysUser = sysUserService.findByTelephoneOrMail(loginForm.getLoginId());
        if (sysUser == null || SysUserStatus.valueOf(sysUser.getStatus()) == SysUserStatus.DELETED) {
            return SysResponseCode.SYS_USER_NOT_EXIST;
        } else if (!DataUtils.md5(loginForm.getPassword()).equals(sysUser.getPassword())) {
            return SysResponseCode.SYS_USER_PASSWORD_ERROR;
        } else if (SysUserStatus.valueOf(sysUser.getStatus()) == SysUserStatus.FROZEN) {
            return SysResponseCode.SYS_USER_FROZEN;
        } else {
            AdminAuthorizationInfo authorizationInfo = new AdminAuthorizationInfo();
            SimpleUserInfo userInfo = new SimpleUserInfo();
            userInfo.setId(sysUser.getId());
            userInfo.setUsername(sysUser.getUsername());
            authorizationInfo.setUserInfo(userInfo);
            session.setAttribute(AdminConstants.SYS_USER_SESSION_KEY, authorizationInfo);
        }
        Map<String, Object> data = Maps.newHashMap();
        data.put("user", MapperUtils.map(sysUser, SysUserDto.class));
        data.put("acls", sysTreeService.userAclTree(sysUser.getId()));
        data.put("roles", sysRoleService.getRoleListByUserId(sysUser.getId()));
        return ResponseCode.buildResponse(data);
    }

    @ApiOperation(value = "登出")
    @PostMapping(value = "/logout.json")
    public ResponseCode logout(HttpSession session) {
        session.invalidate();
        return ResponseCode.SUCCESS;
    }

    @ApiOperation(value = "添加系统用户")
    @ApiImplicitParam(name = "param", value = "用户参数", required = true, dataType = "UserForm")
    @PostMapping(value = "/save.json")
    public ResponseCode saveUser(@RequestBody @Valid UserForm userForm, BindingResult bindingResult, AuthenticationInfo authenticationInfo, HttpServletRequest request) {
        ParamValidator.validate(bindingResult);
        SysUserDto sysUserDto = MapperUtils.map(userForm, SysUserDto.class);
        sysUserDto.setOperator(authenticationInfo.getUserName());
        sysUserDto.setOperateIp(IpUtils.getClientIpAddr(request));
        sysUserService.add(sysUserDto);
        return ResponseCode.SUCCESS;
    }

    @ApiOperation(value = "修改系统用户")
    @ApiImplicitParam(name = "param", value = "用户参数", required = true, dataType = "UserForm")
    @PostMapping(value = "/update.json")
    public ResponseCode updateUser(@RequestBody @Valid UserForm userForm, BindingResult bindingResult, AuthenticationInfo authenticationInfo, HttpServletRequest request) {
        ParamValidator.validate(bindingResult);
        SysUserDto sysUserDto = MapperUtils.map(userForm, SysUserDto.class);
        sysUserDto.setOperator(authenticationInfo.getUserName());
        sysUserDto.setOperateIp(IpUtils.getClientIpAddr(request));
        sysUserService.update(sysUserDto);
        return ResponseCode.SUCCESS;
    }

    @ApiOperation(value = "分页查询系统用户")
    @ApiImplicitParam(name = "param", value = "查询条件", required = true, dataType = "UserQueryForm")
    @PostMapping(value = "/page.json")
    public PageResultDto<SysUserDto> page(@Valid @RequestBody UserQueryForm userQueryForm, BindingResult bindingResult) {
        ParamValidator.validate(bindingResult);
        UserQueryDto userQueryDto = MapperUtils.map(userQueryForm, UserQueryDto.class);
        return sysUserService.query(userQueryDto);
    }

    @ApiOperation(value = "获某个用户具备的角色和权限（树形结构)")
    @ApiImplicitParam(name = "param", value = "用户id", required = true, dataType = "UserIdForm")
    @PostMapping(value = "/acl.json")
    public ResponseCode acls(@Valid @RequestBody UserIdForm userIdForm, BindingResult bindingResult) {
        ParamValidator.validate(bindingResult);
        Map<String, Object> data = Maps.newHashMap();
        data.put("acls", sysTreeService.userAclTree(userIdForm.getUserId()));
        data.put("roles", sysRoleService.getRoleListByUserId(userIdForm.getUserId()));
        return ResponseCode.buildResponse(data);
    }
}
