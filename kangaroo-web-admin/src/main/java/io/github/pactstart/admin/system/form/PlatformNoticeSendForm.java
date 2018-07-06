package io.github.pactstart.admin.system.form;

import javax.validation.constraints.NotNull;

public class PlatformNoticeSendForm {

    @NotNull(message = "未选择需要发送的平台通知")
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
