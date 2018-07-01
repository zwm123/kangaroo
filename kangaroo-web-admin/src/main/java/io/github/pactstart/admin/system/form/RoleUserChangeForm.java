package io.github.pactstart.admin.system.form;

import javax.validation.constraints.NotNull;
import java.util.List;

public class RoleUserChangeForm {

    @NotNull(message = "roleId不能为空")
    private Integer roleId;

    private List<Integer> userList;
}
