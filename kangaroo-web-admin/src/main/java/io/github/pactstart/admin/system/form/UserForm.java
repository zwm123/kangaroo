package io.github.pactstart.admin.system.form;

import io.github.pactstart.biz.common.validation.group.Common;
import io.github.pactstart.biz.common.validation.group.Update;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserForm {

    @NotNull(message = "用户id不能为空", groups = {Update.class})
    private Integer id;

    @NotBlank(message = "用户名不可以为空", groups = {Common.class})
    @Length(min = 6, max = 20, message = "用户名长度为6-20个字符")
    private String username;

    @NotBlank(message = "密码不能为空", groups = {Common.class})
    @Pattern(regexp = "[A-Za-z0-9]{6,20}", message = "密码由6-20位字母或数字组成")
    private String password;

    @NotBlank(message = "电话不可以为空", groups = {Common.class})
    @Length(min = 11, max = 11, message = "电话需要11个字")
    private String telephone;

    @NotBlank(message = "邮箱不允许为空", groups = {Common.class})
    @Length(min = 5, max = 50, message = "邮箱长度需要在50个字符以内")
    @Email(message = "邮箱格式不正确")
    private String mail;

    @NotNull(message = "必须提供用户所在的部门", groups = {Common.class})
    private Integer deptId;

    @NotNull(message = "必须指定用户的状态", groups = {Common.class})
    private Integer status;

    @Length(min = 0, max = 200, message = "备注长度需要在200个字以内", groups = {Common.class})
    private String remark = "";

}
