package io.github.pactstart.admin.system.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class PlatformNoticeSendForm {

    @Size(min = 1, message = "未选择需要发送的用户")
    @NotNull
    private List<Integer> memberIdList;

    @NotNull(message = "未选择需要发送的平台通知")
    private Integer platformNoticeId;

    public List<Integer> getMemberIdList() {
        return memberIdList;
    }

    public void setMemberIdList(List<Integer> memberIdList) {
        this.memberIdList = memberIdList;
    }

    public Integer getPlatformNoticeId() {
        return platformNoticeId;
    }

    public void setPlatformNoticeId(Integer platformNoticeId) {
        this.platformNoticeId = platformNoticeId;
    }
}
